package junseop.timerangechecker.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "daily_stats")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyStats extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private LocalDate date;

  @Column(nullable = false)
  @Builder.Default
  private Integer totalRequests = 0;

  @Column(nullable = false)
  @Builder.Default
  private Integer uniqueSessions = 0;

  @Column(length = 10)
  private String popularRange;

  public static DailyStats create(LocalDate date) {
    return DailyStats.builder()
                     .date(date)
                     .totalRequests(1)
                     .uniqueSessions(1)
                     .build();
  }

  public void incrementRequest(String rangeKey, boolean isNewSession) {
    this.totalRequests++;
    if (isNewSession) {
      this.uniqueSessions++;
    }
    this.popularRange = rangeKey;
  }

  public void updatePopularRange(String rangeKey) {
    this.popularRange = rangeKey;
  }
} 