package junseop.timerangechecker.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeCheckRequest {

  @NotNull(message = "対象時間は必須です。")
  @Min(value = 0, message = "時間は0から23の間で入力してください。")
  @Max(value = 23, message = "時間は0から23の間で入力してください。")
  private Integer targetHour;

  @NotNull(message = "開始時間は必須です。")
  @Min(value = 0, message = "時間は0から23の間で入力してください。")
  @Max(value = 23, message = "時間は0から23の間で入力してください。")
  private Integer startHour;

  @NotNull(message = "終了時間は必須です。")
  @Min(value = 0, message = "時間は0から23の間で入力してください。")
  @Max(value = 23, message = "時間は0から23の間で入力してください。")
  private Integer endHour;
} 