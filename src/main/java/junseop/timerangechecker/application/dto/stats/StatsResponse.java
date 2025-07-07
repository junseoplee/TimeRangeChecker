package junseop.timerangechecker.application.dto.stats;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 統計応答 DTO
 * 전체 통계 정보를 포함하는 응답 객체
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsResponse {

  /**
   * 총 리퀘스트 수
   */
  private Long totalRequests;

  /**
   * 유니크 세션 수
   */
  private Long uniqueSessions;

  /**
   * 인기 시간 범위 목록
   */
  private List<RangeStatsDto> popularRanges;

  /**
   * 일별 통계 목록
   */
  private List<DailyStatsDto> dailyStats;
} 