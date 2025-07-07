package junseop.timerangechecker.application.dto.stats;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 日別統計 DTO
 * 특정 날짜의 요청 수, 세션 수, 인기 시간 범위 등의 통계 정보
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyStatsDto {
  
  /**
   * 統計対象日付
   */
  private LocalDate date;
  
  /**
   * 該当日の総リクエスト数
   */
  private Integer totalRequests;
  
  /**
   * 該当日のユニークセッション数
   */
  private Integer uniqueSessions;
  
  /**
   * 該当日の最も人気のある時間範囲
   */
  private String popularRange;
} 