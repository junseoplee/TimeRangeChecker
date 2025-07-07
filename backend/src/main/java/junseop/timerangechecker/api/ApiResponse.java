package junseop.timerangechecker.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {

  private final int code;
  private final String message;
  private final T data;

  private ApiResponse(HttpStatus status, String message, T data) {
    this.code = status.value();
    this.message = message;
    this.data = data;
  }

  public static <T> ApiResponse<T> OK(T data) {
    return new ApiResponse<>(HttpStatus.OK, "リクエストは正常に処理されました。", data);
  }

  public static <T> ApiResponse<T> OK(T data, String message) {
    return new ApiResponse<>(HttpStatus.OK, message, data);
  }

  public static <T> ApiResponse<T> CREATED(T data) {
    return new ApiResponse<>(HttpStatus.CREATED, "リソースが正常に作成されました。", data);
  }

  public static <T> ApiResponse<T> NO_CONTENT() {
    return new ApiResponse<>(HttpStatus.NO_CONTENT, "処理は正常に完了しました。", null);
  }
}
