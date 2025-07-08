package junseop.timerangechecker.application.dto.stats;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 統計応答 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsResponse {

  /**
   * 総リクエスト数
   */
  private Long totalRequests;

  /**
   * ユニークセッション数
   */
  private Long uniqueSessions;

  /**
   * 人気時間範囲リスト
   */
  private List<RangeStatsDto> popularRanges;

  /**
   * 日別統計リスト
   */
  private List<DailyStatsDto> dailyStats;
} 