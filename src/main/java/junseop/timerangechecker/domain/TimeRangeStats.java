package junseop.timerangechecker.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "time_range_stats")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeRangeStats extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 10)
  private String rangeKey;

  @Column(nullable = false)
  @Builder.Default
  private Integer requestCount = 0;

  @Column(nullable = false)
  @Builder.Default
  private Integer matchCount = 0;

  @Column(nullable = false)
  private LocalDateTime lastAccessed;

  public static TimeRangeStats create(String rangeKey) {
    return TimeRangeStats.builder()
                         .rangeKey(rangeKey)
                         .requestCount(1)
                         .matchCount(0)
                         .lastAccessed(LocalDateTime.now())
                         .build();
  }

  public static String createRangeKey(Integer startHour, Integer endHour) {
    return String.format("%02d-%02d", startHour, endHour);
  }

  public void incrementRequest(Boolean isMatch) {
    this.requestCount++;
    if (isMatch) {
      this.matchCount++;
    }
    this.lastAccessed = LocalDateTime.now();
  }

  public double getMatchRate() {
    if (requestCount == 0) {
      return 0.0;
    }
    return (double) matchCount / requestCount * 100;
  }
} 