package junseop.timerangechecker.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "time_check_logs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeCheckLog extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Integer targetHour;

  @Column(nullable = false)
  private Integer startHour;

  @Column(nullable = false)
  private Integer endHour;

  @Column(nullable = false)
  private Boolean isIncluded;

  @Column(length = 45)
  private String clientIp;

  @Column(length = 100)
  private String sessionId;

  public static TimeCheckLog create(Integer targetHour, Integer startHour, Integer endHour, 
                                    Boolean isIncluded, String clientIp, String sessionId) {
    return TimeCheckLog.builder()
                       .targetHour(targetHour)
                       .startHour(startHour)
                       .endHour(endHour)
                       .isIncluded(isIncluded)
                       .clientIp(clientIp)
                       .sessionId(sessionId)
                       .build();
  }
} 