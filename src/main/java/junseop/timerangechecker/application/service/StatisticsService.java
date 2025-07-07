package junseop.timerangechecker.application.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import junseop.timerangechecker.application.dto.stats.DailyStatsDto;
import junseop.timerangechecker.application.dto.stats.RangeStatsDto;
import junseop.timerangechecker.application.dto.stats.StatsResponse;
import junseop.timerangechecker.domain.DailyStats;
import junseop.timerangechecker.domain.TimeRangeStats;
import junseop.timerangechecker.domain.repository.DailyStatsRepository;
import junseop.timerangechecker.domain.repository.TimeCheckLogRepository;
import junseop.timerangechecker.domain.repository.TimeRangeStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {

  private final TimeRangeStatsRepository timeRangeStatsRepository;
  private final DailyStatsRepository dailyStatsRepository;
  private final TimeCheckLogRepository timeCheckLogRepository;
  
  // 今日処理されたセッションIDを一時保存（実際の運用時にはRedis等を使用）
  private final Set<String> todayProcessedSessions = new HashSet<>();

  /**
   * 統計情報更新
   */
  @Transactional
  public void updateStatistics(Integer startHour, Integer endHour, Boolean isIncluded, HttpServletRequest request) {
    try {
      String rangeKey = TimeRangeStats.createRangeKey(startHour, endHour);
      String sessionId = getSessionId(request);
      LocalDate today = LocalDate.now();

      // 時間範囲統計更新
      updateTimeRangeStats(rangeKey, isIncluded);

      // 日間統計更新
      updateDailyStats(today, rangeKey, sessionId);

    } catch (Exception e) {
      log.error("統計更新失敗", e);
      // 統計更新失敗はメイン機能に影響を与えないよう処理
    }
  }

  /**
   * 時間範囲統計更新
   */
  private void updateTimeRangeStats(String rangeKey, Boolean isIncluded) {
    Optional<TimeRangeStats> existingStats = timeRangeStatsRepository.findByRangeKeyAndIsActiveTrue(rangeKey);
    
    if (existingStats.isPresent()) {
      existingStats.get().incrementRequest(isIncluded);
      timeRangeStatsRepository.save(existingStats.get());
    } else {
      TimeRangeStats newStats = TimeRangeStats.create(rangeKey);
      if (isIncluded) {
        newStats.incrementRequest(true);
      }
      timeRangeStatsRepository.save(newStats);
    }
  }

  /**
   * 日間統計更新
   */
  private void updateDailyStats(LocalDate date, String rangeKey, String sessionId) {
    Optional<DailyStats> existingStats = dailyStatsRepository.findByDateAndIsActiveTrue(date);
    boolean isNewSession = sessionId != null && !todayProcessedSessions.contains(sessionId);
    
    if (existingStats.isPresent()) {
      existingStats.get().incrementRequest(rangeKey, isNewSession);
      dailyStatsRepository.save(existingStats.get());
    } else {
      DailyStats newStats = DailyStats.create(date);
      newStats.incrementRequest(rangeKey, isNewSession);
      dailyStatsRepository.save(newStats);
    }

    // セッションIDを今日処理済リストに追加
    if (sessionId != null) {
      todayProcessedSessions.add(sessionId);
    }
  }

  /**
   * 全体統計照会
   */
  @Transactional(readOnly = true)
  public StatsResponse getOverallStats() {
    try {
      // 期間別総要求数とセッション数計算（最近30日）
      LocalDate endDate = LocalDate.now();
      LocalDate startDate = endDate.minusDays(30);
      
      Long totalRequests = getTotalRequestsInPeriod(startDate, endDate);
      Long uniqueSessions = getUniqueSessionsInPeriod(startDate, endDate);

      // 人気時間範囲照会（上位10個）
      List<RangeStatsDto> popularRanges = getPopularRanges(10);

      // 日別統計照会（最近7日）
      List<DailyStatsDto> dailyStats = getDailyStats(endDate.minusDays(7), endDate);

      return StatsResponse.builder()
                          .totalRequests(totalRequests)
                          .uniqueSessions(uniqueSessions)
                          .popularRanges(popularRanges)
                          .dailyStats(dailyStats)
                          .build();

    } catch (Exception e) {
      log.error("統計照会失敗", e);
      throw new RuntimeException("統計データの取得に失敗しました。", e);
    }
  }

  /**
   * 特定期間の総要求数計算
   */
  private Long getTotalRequestsInPeriod(LocalDate startDate, LocalDate endDate) {
    LocalDateTime startDateTime = startDate.atStartOfDay();
    LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
    
    return timeCheckLogRepository.countByCreatedAtBetweenAndIsActiveTrue(startDateTime, endDateTime);
  }

  /**
   * 特定期間のユニークセッション数計算
   */
  private Long getUniqueSessionsInPeriod(LocalDate startDate, LocalDate endDate) {
    LocalDateTime startDateTime = startDate.atStartOfDay();
    LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
    
    return timeCheckLogRepository.countDistinctSessionByCreatedAtBetweenAndIsActiveTrue(startDateTime, endDateTime);
  }

  /**
   * 人気時間範囲照会
   */
  private List<RangeStatsDto> getPopularRanges(int limit) {
    List<TimeRangeStats> rangeStatsList = timeRangeStatsRepository.findByIsActiveTrueOrderByRequestCountDesc();
    
    return rangeStatsList.stream()
                         .limit(limit)
                         .map(this::convertToRangeStatsDto)
                         .collect(Collectors.toList());
  }

  /**
   * 日別統計照会
   */
  private List<DailyStatsDto> getDailyStats(LocalDate startDate, LocalDate endDate) {
    List<DailyStats> dailyStatsList = dailyStatsRepository.findByDateBetweenAndIsActiveTrueOrderByDateDesc(startDate, endDate);
    
    return dailyStatsList.stream()
                         .map(this::convertToDailyStatsDto)
                         .collect(Collectors.toList());
  }

  /**
   * TimeRangeStatsをDTOに変換
   */
  private RangeStatsDto convertToRangeStatsDto(TimeRangeStats stats) {
    return RangeStatsDto.builder()
                        .rangeKey(stats.getRangeKey())
                        .requestCount(stats.getRequestCount())
                        .matchCount(stats.getMatchCount())
                        .matchRate(stats.getMatchRate())
                        .lastAccessed(stats.getLastAccessed())
                        .build();
  }

  /**
   * DailyStatsをDTOに変換
   */
  private DailyStatsDto convertToDailyStatsDto(DailyStats stats) {
    return DailyStatsDto.builder()
                        .date(stats.getDate())
                        .totalRequests(stats.getTotalRequests())
                        .uniqueSessions(stats.getUniqueSessions())
                        .popularRange(stats.getPopularRange())
                        .build();
  }

  /**
   * セッションID抽出
   */
  private String getSessionId(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    return session != null ? session.getId() : null;
  }
} 