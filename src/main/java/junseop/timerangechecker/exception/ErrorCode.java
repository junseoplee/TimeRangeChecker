package junseop.timerangechecker.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "アクセス権限がありません。");

  private final HttpStatus httpStatus;
  private final String message;
}
