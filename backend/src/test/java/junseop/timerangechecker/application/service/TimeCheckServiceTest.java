package junseop.timerangechecker.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import junseop.timerangechecker.application.dto.TimeCheckRequest;
import junseop.timerangechecker.application.dto.TimeCheckResponse;
import junseop.timerangechecker.domain.repository.TimeCheckLogRepository;
import junseop.timerangechecker.exception.BusinessException;
import junseop.timerangechecker.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("時間チェックサービステスト")
class TimeCheckServiceTest {

  @Mock
  private TimeCheckLogRepository timeCheckLogRepository;

  @Mock
  private StatisticsService statisticsService;

  @Mock
  private CacheService cacheService;

  @InjectMocks
  private TimeCheckService timeCheckService;

  private HttpServletRequest mockRequest;
  private HttpSession mockSession;

  @BeforeEach
  void setUp() {
    mockRequest = mock(HttpServletRequest.class);
    mockSession = mock(HttpSession.class);
    
    lenient().when(mockRequest.getSession(false)).thenReturn(mockSession);
    lenient().when(mockSession.getId()).thenReturn("test-session-id");
    lenient().when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");
    
    lenient().doNothing().when(statisticsService).updateStatistics(any(), any(), any(), any());
    
    // CacheService 모킹 설정 - 기본적으로 캐시 미스 상황으로 설정
    lenient().when(cacheService.getCachedTimeCheckResult(any(), any(), any())).thenReturn(null);
    lenient().doNothing().when(cacheService).cacheTimeCheckResult(any(), any(), any(), any());
  }

  @Nested
  @DisplayName("通常時間範囲テスト")
  class NormalTimeRangeTest {

    @Test
    @DisplayName("14時が9時から17時の範囲内に含まれる")
    void shouldInclude14InRange9To17() {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(14)
                                                 .startHour(9)
                                                 .endHour(17)
                                                 .build();

      // When
      TimeCheckResponse response = timeCheckService.checkTimeInRange(request, mockRequest);

      // Then
      assertThat(response.getIsIncluded()).isTrue();
      assertThat(response.getRangeType()).isEqualTo("NORMAL");
      assertThat(response.getTargetHour()).isEqualTo(14);
      assertThat(response.getStartHour()).isEqualTo(9);
      assertThat(response.getEndHour()).isEqualTo(17);
      assertThat(response.getDescription()).contains("含まれます");
    }

    @Test
    @DisplayName("9時が9時から17時の範囲内に含まれる（開始時刻）")
    void shouldInclude9InRange9To17_StartTime() {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(9)
                                                 .startHour(9)
                                                 .endHour(17)
                                                 .build();

      // When
      TimeCheckResponse response = timeCheckService.checkTimeInRange(request, mockRequest);

      // Then
      assertThat(response.getIsIncluded()).isTrue();
      assertThat(response.getRangeType()).isEqualTo("NORMAL");
    }

    @Test
    @DisplayName("17時が9時から17時の範囲内に含まれない（終了時刻）")
    void shouldNotInclude17InRange9To17_EndTime() {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(17)
                                                 .startHour(9)
                                                 .endHour(17)
                                                 .build();

      // When
      TimeCheckResponse response = timeCheckService.checkTimeInRange(request, mockRequest);

      // Then
      assertThat(response.getIsIncluded()).isFalse();
      assertThat(response.getRangeType()).isEqualTo("NORMAL");
      assertThat(response.getDescription()).contains("含まれません");
    }

    @Test
    @DisplayName("8時が9時から17時の範囲内に含まれない")
    void shouldNotInclude8InRange9To17() {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(8)
                                                 .startHour(9)
                                                 .endHour(17)
                                                 .build();

      // When
      TimeCheckResponse response = timeCheckService.checkTimeInRange(request, mockRequest);

      // Then
      assertThat(response.getIsIncluded()).isFalse();
      assertThat(response.getRangeType()).isEqualTo("NORMAL");
    }
  }

  @Nested
  @DisplayName("深夜跨ぎ時間範囲テスト")
  class MidnightCrossingTimeRangeTest {

    @Test
    @DisplayName("2時が22時から5時の範囲内に含まれる")
    void shouldInclude2InRange22To5() {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(2)
                                                 .startHour(22)
                                                 .endHour(5)
                                                 .build();

      // When
      TimeCheckResponse response = timeCheckService.checkTimeInRange(request, mockRequest);

      // Then
      assertThat(response.getIsIncluded()).isTrue();
      assertThat(response.getRangeType()).isEqualTo("MIDNIGHT_CROSSING");
      assertThat(response.getDescription()).contains("翌日");
    }

    @Test
    @DisplayName("23時が22時から5時の範囲内に含まれる")
    void shouldInclude23InRange22To5() {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(23)
                                                 .startHour(22)
                                                 .endHour(5)
                                                 .build();

      // When
      TimeCheckResponse response = timeCheckService.checkTimeInRange(request, mockRequest);

      // Then
      assertThat(response.getIsIncluded()).isTrue();
      assertThat(response.getRangeType()).isEqualTo("MIDNIGHT_CROSSING");
    }

    @Test
    @DisplayName("22時が22時から5時の範囲内に含まれる（開始時刻）")
    void shouldInclude22InRange22To5_StartTime() {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(22)
                                                 .startHour(22)
                                                 .endHour(5)
                                                 .build();

      // When
      TimeCheckResponse response = timeCheckService.checkTimeInRange(request, mockRequest);

      // Then
      assertThat(response.getIsIncluded()).isTrue();
      assertThat(response.getRangeType()).isEqualTo("MIDNIGHT_CROSSING");
    }

    @Test
    @DisplayName("5時が22時から5時の範囲内に含まれない（終了時刻）")
    void shouldNotInclude5InRange22To5_EndTime() {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(5)
                                                 .startHour(22)
                                                 .endHour(5)
                                                 .build();

      // When
      TimeCheckResponse response = timeCheckService.checkTimeInRange(request, mockRequest);

      // Then
      assertThat(response.getIsIncluded()).isFalse();
      assertThat(response.getRangeType()).isEqualTo("MIDNIGHT_CROSSING");
    }

    @Test
    @DisplayName("12時が22時から5時の範囲内に含まれない")
    void shouldNotInclude12InRange22To5() {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(12)
                                                 .startHour(22)
                                                 .endHour(5)
                                                 .build();

      // When
      TimeCheckResponse response = timeCheckService.checkTimeInRange(request, mockRequest);

      // Then
      assertThat(response.getIsIncluded()).isFalse();
      assertThat(response.getRangeType()).isEqualTo("MIDNIGHT_CROSSING");
    }
  }

  @Nested
  @DisplayName("24時間全体範囲テスト")
  class FullDayTimeRangeTest {

    @Test
    @DisplayName("12時が0時から0時の範囲内に含まれる（24時間全体）")
    void shouldInclude12InRange0To0_FullDay() {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(12)
                                                 .startHour(0)
                                                 .endHour(0)
                                                 .build();

      // When
      TimeCheckResponse response = timeCheckService.checkTimeInRange(request, mockRequest);

      // Then
      assertThat(response.getIsIncluded()).isTrue();
      assertThat(response.getRangeType()).isEqualTo("FULL_DAY");
      assertThat(response.getDescription()).contains("24時間全体");
    }

    @Test
    @DisplayName("0時が15時から15時の範囲内に含まれる（24時間全体）")
    void shouldInclude0InRange15To15_FullDay() {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(0)
                                                 .startHour(15)
                                                 .endHour(15)
                                                 .build();

      // When
      TimeCheckResponse response = timeCheckService.checkTimeInRange(request, mockRequest);

      // Then
      assertThat(response.getIsIncluded()).isTrue();
      assertThat(response.getRangeType()).isEqualTo("FULL_DAY");
    }

    @Test
    @DisplayName("23時が23時から23時の範囲内に含まれる（24時間全体）")
    void shouldInclude23InRange23To23_FullDay() {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(23)
                                                 .startHour(23)
                                                 .endHour(23)
                                                 .build();

      // When
      TimeCheckResponse response = timeCheckService.checkTimeInRange(request, mockRequest);

      // Then
      assertThat(response.getIsIncluded()).isTrue();
      assertThat(response.getRangeType()).isEqualTo("FULL_DAY");
    }
  }

  @Nested
  @DisplayName("入力有効性検証テスト")
  class InputValidationTest {

    @Test
    @DisplayName("対象時間がnullの場合例外発生")
    void shouldThrowExceptionWhenTargetHourIsNull() {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(null)
                                                 .startHour(9)
                                                 .endHour(17)
                                                 .build();

      // When & Then
      assertThatThrownBy(() -> timeCheckService.checkTimeInRange(request, mockRequest))
          .isInstanceOf(BusinessException.class)
          .hasFieldOrPropertyWithValue("code", ErrorCode.MISSING_REQUIRED_PARAMETER.getHttpStatus().value());
    }

    @Test
    @DisplayName("開始時間がnullの場合例外発生")
    void shouldThrowExceptionWhenStartHourIsNull() {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(14)
                                                 .startHour(null)
                                                 .endHour(17)
                                                 .build();

      // When & Then
      assertThatThrownBy(() -> timeCheckService.checkTimeInRange(request, mockRequest))
          .isInstanceOf(BusinessException.class)
          .hasFieldOrPropertyWithValue("code", ErrorCode.MISSING_REQUIRED_PARAMETER.getHttpStatus().value());
    }

    @Test
    @DisplayName("時間が24以上の場合例外発生")
    void shouldThrowExceptionWhenHourIsGreaterThan23() {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(24)
                                                 .startHour(9)
                                                 .endHour(17)
                                                 .build();

      // When & Then
      assertThatThrownBy(() -> timeCheckService.checkTimeInRange(request, mockRequest))
          .isInstanceOf(BusinessException.class)
          .hasFieldOrPropertyWithValue("code", ErrorCode.INVALID_HOUR_RANGE.getHttpStatus().value());
    }

    @Test
    @DisplayName("時間が負数の場合例外発生")
    void shouldThrowExceptionWhenHourIsNegative() {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(-1)
                                                 .startHour(9)
                                                 .endHour(17)
                                                 .build();

      // When & Then
      assertThatThrownBy(() -> timeCheckService.checkTimeInRange(request, mockRequest))
          .isInstanceOf(BusinessException.class)
          .hasFieldOrPropertyWithValue("code", ErrorCode.INVALID_HOUR_RANGE.getHttpStatus().value());
    }
  }

  @Nested
  @DisplayName("境界値テスト")
  class BoundaryValueTest {

    @Test
    @DisplayName("0時が0時から1時の範囲内に含まれる")
    void shouldInclude0InRange0To1() {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(0)
                                                 .startHour(0)
                                                 .endHour(1)
                                                 .build();

      // When
      TimeCheckResponse response = timeCheckService.checkTimeInRange(request, mockRequest);

      // Then
      assertThat(response.getIsIncluded()).isTrue();
      assertThat(response.getRangeType()).isEqualTo("NORMAL");
    }

    @Test
    @DisplayName("23時が22時から23時の範囲内に含まれない")
    void shouldNotInclude23InRange22To23() {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(23)
                                                 .startHour(22)
                                                 .endHour(23)
                                                 .build();

      // When
      TimeCheckResponse response = timeCheckService.checkTimeInRange(request, mockRequest);

      // Then
      assertThat(response.getIsIncluded()).isFalse();
      assertThat(response.getRangeType()).isEqualTo("NORMAL");
    }

    @Test
    @DisplayName("0時が23時から0時の範囲内に含まれない（深夜跨ぎ）")
    void shouldNotInclude0InRange23To0_MidnightCrossing() {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(0)
                                                 .startHour(23)
                                                 .endHour(0)
                                                 .build();

      // When
      TimeCheckResponse response = timeCheckService.checkTimeInRange(request, mockRequest);

      // Then
      assertThat(response.getIsIncluded()).isFalse();
      assertThat(response.getRangeType()).isEqualTo("MIDNIGHT_CROSSING");
    }
  }

  @Test
  @DisplayName("ログ保存とサービス呼び出し確認")
  void shouldSaveLogAndCallStatisticsService() {
    // Given
    TimeCheckRequest request = TimeCheckRequest.builder()
                                               .targetHour(14)
                                               .startHour(9)
                                               .endHour(17)
                                               .build();

    // When
    timeCheckService.checkTimeInRange(request, mockRequest);

    // Then
    verify(timeCheckLogRepository).save(any());
    verify(statisticsService).updateStatistics(9, 17, true, mockRequest);
  }
} 