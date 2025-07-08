package junseop.timerangechecker.api.controller;

import junseop.timerangechecker.api.ApiResponse;
import junseop.timerangechecker.application.dto.stats.StatsResponse;
import junseop.timerangechecker.application.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Slf4j
public class StatisticsController {

  private final StatisticsService statisticsService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<StatsResponse> getOverallStatistics() {
    log.info("全体統計照会要求");
    
    StatsResponse response = statisticsService.getOverallStats();
    
    log.info("全体統計照会完了 - 総要求数: {}, ユニークセッション: {}", 
             response.getTotalRequests(), response.getUniqueSessions());
    
    return ApiResponse.OK(response);
  }

  @GetMapping("/health")
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<Object> getStatisticsHealth() {
    log.info("統計サービス状態確認要求");
    
    return ApiResponse.OK(new Object() {
      public final String status = "OK";
      public final String message = "統計サービスは正常に動作しています。";
    });
  }
} 