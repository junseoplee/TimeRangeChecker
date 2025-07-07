package junseop.timerangechecker.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import junseop.timerangechecker.api.ApiResponse;
import junseop.timerangechecker.application.dto.TimeCheckRequest;
import junseop.timerangechecker.application.dto.TimeCheckResponse;
import junseop.timerangechecker.application.service.TimeCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/time-check")
@RequiredArgsConstructor
@Slf4j
public class TimeCheckController {

  private final TimeCheckService timeCheckService;

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<TimeCheckResponse> checkTimeInRange(
      @Valid @RequestBody TimeCheckRequest request,
      HttpServletRequest httpRequest) {

    log.info("時間チェック要求受信: {}", request);
    
    TimeCheckResponse response = timeCheckService.checkTimeInRange(request, httpRequest);
    
    log.info("時間チェック応答: {}", response);
    
    return ApiResponse.OK(response);
  }
} 