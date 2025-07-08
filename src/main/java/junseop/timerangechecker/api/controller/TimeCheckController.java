package junseop.timerangechecker.api.controller;

import junseop.timerangechecker.api.ApiResponse;
import junseop.timerangechecker.application.dto.TimeCheckRequest;
import junseop.timerangechecker.application.dto.TimeCheckResponse;
import junseop.timerangechecker.application.service.TimeCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/time-check")
@RequiredArgsConstructor
@Slf4j
public class TimeCheckController {

  private final TimeCheckService timeCheckService;

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<TimeCheckResponse> checkTimeRange(@RequestBody TimeCheckRequest request) {
    log.info("時間範囲チェック要求: 開始時刻={}, 終了時刻={}", request.getStartTime(), request.getEndTime());
    
    TimeCheckResponse response = timeCheckService.checkTimeRange(request);
    
    log.info("時間範囲チェック完了: 結果={}", response.isInRange() ? "範囲内" : "範囲外");
    
    return ApiResponse.OK(response);
  }
} 