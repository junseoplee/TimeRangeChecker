package junseop.timerangechecker.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "アクセス権限がありません。"),
  
  // 時間チェック関連エラー
  INVALID_HOUR_RANGE(HttpStatus.BAD_REQUEST, "時間は0から23の間で入力してください。"),
  INVALID_TIME_FORMAT(HttpStatus.BAD_REQUEST, "時間形式が正しくありません。"),
  MISSING_REQUIRED_PARAMETER(HttpStatus.BAD_REQUEST, "必須パラメータが不足しています。"),
  
  // 統計関連エラー
  STATS_NOT_FOUND(HttpStatus.NOT_FOUND, "統計データが見つかりません。"),
  INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "日付範囲が正しくありません。"),
  
  // システムエラー
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "サーバー内部エラーが発生しました。"),
  DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "データベースエラーが発生しました。");

  private final HttpStatus httpStatus;
  private final String message;
}
