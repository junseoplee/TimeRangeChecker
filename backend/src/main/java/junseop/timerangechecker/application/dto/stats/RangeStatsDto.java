package junseop.timerangechecker.application.dto.stats;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 時間範囲統計 DTO
 * 특정 시간 범위에 대한 요청 수, 매치 수, 매치율 등의 통계 정보
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RangeStatsDto {
  
  /**
   * 時間範囲キー (例: "09-17", "22-05")
   */
  private String rangeKey;
  
  /**
   * 該当範囲への総リクエスト数
   */
  private Integer requestCount;
  
  /**
   * 該当範囲内でマッチした回数
   */
  private Integer matchCount;
  
  /**
   * マッチ率 (matchCount / requestCount * 100)
   */
  private Double matchRate;
  
  /**
   * 最終アクセス時間
   */
  private LocalDateTime lastAccessed;
} 