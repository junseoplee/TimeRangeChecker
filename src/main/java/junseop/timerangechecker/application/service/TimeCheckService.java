package junseop.timerangechecker.application.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import junseop.timerangechecker.application.dto.TimeCheckRequest;
import junseop.timerangechecker.application.dto.TimeCheckResponse;
import junseop.timerangechecker.application.dto.TimeRangeType;
import junseop.timerangechecker.domain.TimeCheckLog;
import junseop.timerangechecker.domain.repository.TimeCheckLogRepository;
import junseop.timerangechecker.exception.BusinessException;
import junseop.timerangechecker.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeCheckService {

  private final TimeCheckLogRepository timeCheckLogRepository;
  private final StatisticsService statisticsService;

  /**
   * 時間チェックメインロジック
   * 特定時刻が指定された時間範囲内に含まれるかを判定
   */
  @Transactional
  public TimeCheckResponse checkTimeInRange(TimeCheckRequest request, HttpServletRequest httpRequest) {
    log.info("時間チェック要求: targetHour={}, startHour={}, endHour={}", 
             request.getTargetHour(), request.getStartHour(), request.getEndHour());

    // 入力有効性検証
    validateTimeInput(request);

    // 時間範囲タイプ判別
    TimeRangeType rangeType = determineRangeType(request.getStartHour(), request.getEndHour());
    
    // 含有判定
    boolean isIncluded = isTimeIncluded(request.getTargetHour(), request.getStartHour(), 
                                        request.getEndHour(), rangeType);

    // 要求ログ保存
    saveRequestLog(request, isIncluded, httpRequest);

    // 統計更新
    statisticsService.updateStatistics(request.getStartHour(), request.getEndHour(), isIncluded, httpRequest);

    // 応答生成
    TimeCheckResponse response = TimeCheckResponse.of(
        request.getTargetHour(),
        request.getStartHour(),
        request.getEndHour(),
        isIncluded,
        rangeType.getValue()
    );

    log.info("時間チェック結果: isIncluded={}, rangeType={}", isIncluded, rangeType.getValue());
    return response;
  }

  /**
   * 入力有効性検証
   */
  private void validateTimeInput(TimeCheckRequest request) {
    if (request.getTargetHour() == null || request.getStartHour() == null || request.getEndHour() == null) {
      throw new BusinessException(ErrorCode.MISSING_REQUIRED_PARAMETER);
    }

    if (!isValidHour(request.getTargetHour()) || 
        !isValidHour(request.getStartHour()) || 
        !isValidHour(request.getEndHour())) {
      throw new BusinessException(ErrorCode.INVALID_HOUR_RANGE);
    }
  }

  /**
   * 時間有効性検査 (0-23)
   */
  private boolean isValidHour(Integer hour) {
    return hour != null && hour >= 0 && hour <= 23;
  }

  /**
   * 時間範囲タイプ判別
   */
  private TimeRangeType determineRangeType(Integer startHour, Integer endHour) {
    if (startHour.equals(endHour)) {
      return TimeRangeType.FULL_DAY;
    } else if (startHour > endHour) {
      return TimeRangeType.MIDNIGHT_CROSSING;
    } else {
      return TimeRangeType.NORMAL;
    }
  }

  /**
   * 時間含有判定 - コア仕様
   */
  private boolean isTimeIncluded(Integer targetHour, Integer startHour, Integer endHour, TimeRangeType rangeType) {
    return switch (rangeType) {
      case FULL_DAY -> true; // 開始時刻 = 終了時刻の場合24時間全体
      case MIDNIGHT_CROSSING -> targetHour >= startHour || targetHour < endHour; // 深夜跨ぎ
      case NORMAL -> targetHour >= startHour && targetHour < endHour; // 通常範囲
    };
  }

  /**
   * 要求ログ保存
   */
  private void saveRequestLog(TimeCheckRequest request, boolean isIncluded, HttpServletRequest httpRequest) {
    try {
      String clientIp = getClientIp(httpRequest);
      String sessionId = getSessionId(httpRequest);

      TimeCheckLog log = TimeCheckLog.create(
          request.getTargetHour(),
          request.getStartHour(),
          request.getEndHour(),
          isIncluded,
          clientIp,
          sessionId
      );

      timeCheckLogRepository.save(log);
      
    } catch (Exception e) {
      log.error("要求ログ保存失敗", e);
      // ログ保存失敗はメイン機能に影響を与えないよう処理
    }
  }

  /**
   * クライアントIP抽出
   */
  private String getClientIp(HttpServletRequest request) {
    String clientIp = request.getHeader("X-Forwarded-For");
    if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
      clientIp = request.getHeader("Proxy-Client-IP");
    }
    if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
      clientIp = request.getHeader("WL-Proxy-Client-IP");
    }
    if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
      clientIp = request.getRemoteAddr();
    }
    return clientIp;
  }

  /**
   * セッションID抽出
   */
  private String getSessionId(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    return session != null ? session.getId() : null;
  }
} 