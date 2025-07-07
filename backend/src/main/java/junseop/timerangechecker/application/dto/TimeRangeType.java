package junseop.timerangechecker.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TimeRangeType {
  NORMAL("NORMAL", "通常の時間範囲"),
  MIDNIGHT_CROSSING("MIDNIGHT_CROSSING", "深夜を跨ぐ時間範囲"),
  FULL_DAY("FULL_DAY", "24時間全体");

  private final String value;
  private final String description;
} 