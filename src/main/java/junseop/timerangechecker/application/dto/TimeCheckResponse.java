package junseop.timerangechecker.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeCheckResponse {

  private Integer targetHour;

  private Integer startHour;

  private Integer endHour;

  private Boolean isIncluded;

  private String rangeType;

  private String description;

  public static TimeCheckResponse of(Integer targetHour, Integer startHour, Integer endHour, 
                                     Boolean isIncluded, String rangeType) {
    return TimeCheckResponse.builder()
                            .targetHour(targetHour)
                            .startHour(startHour)
                            .endHour(endHour)
                            .isIncluded(isIncluded)
                            .rangeType(rangeType)
                            .description(generateDescription(targetHour, startHour, endHour, isIncluded, rangeType))
                            .build();
  }

  private static String generateDescription(Integer targetHour, Integer startHour, Integer endHour, 
                                            Boolean isIncluded, String rangeType) {
    String includeText = isIncluded ? "含まれます" : "含まれません";
    String rangeText = switch (rangeType) {
      case "MIDNIGHT_CROSSING" -> String.format("%d時から翌日%d時", startHour, endHour);
      case "FULL_DAY" -> "24時間全体";
      default -> String.format("%d時から%d時", startHour, endHour);
    };
    
    return String.format("対象時間%d時は%sの範囲内に%s。", targetHour, rangeText, includeText);
  }
} 