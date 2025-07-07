package junseop.timerangechecker.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import junseop.timerangechecker.application.dto.TimeCheckRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("時間チェックコントローラー統合テスト")
class TimeCheckControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Nested
  @DisplayName("正常ケーステスト")
  class SuccessfulCases {

    @Test
    @DisplayName("通常時間範囲 - 14時が9時から17時に含まれる")
    void shouldReturnTrueForNormalTimeRange() throws Exception {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(14)
                                                 .startHour(9)
                                                 .endHour(17)
                                                 .build();

      // When & Then
      mockMvc.perform(post("/api/v1/time-check")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(request)))
             .andDo(print())
             .andExpect(status().isOk())
             .andExpect(content().contentType(MediaType.APPLICATION_JSON))
             .andExpect(jsonPath("$.code").value(200))
             .andExpect(jsonPath("$.message").value("リクエストは正常に処理されました。"))
             .andExpect(jsonPath("$.data.targetHour").value(14))
             .andExpect(jsonPath("$.data.startHour").value(9))
             .andExpect(jsonPath("$.data.endHour").value(17))
             .andExpect(jsonPath("$.data.isIncluded").value(true))
             .andExpect(jsonPath("$.data.rangeType").value("NORMAL"))
             .andExpect(jsonPath("$.data.description").value("対象時間14時は9時から17時の範囲内に含まれます。"));
    }

    @Test
    @DisplayName("深夜跨ぎ時間範囲 - 2時が22時から5時に含まれる")
    void shouldReturnTrueForMidnightCrossingTimeRange() throws Exception {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(2)
                                                 .startHour(22)
                                                 .endHour(5)
                                                 .build();

      // When & Then
      mockMvc.perform(post("/api/v1/time-check")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(request)))
             .andDo(print())
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.data.isIncluded").value(true))
             .andExpect(jsonPath("$.data.rangeType").value("MIDNIGHT_CROSSING"))
             .andExpect(jsonPath("$.data.description").value("対象時間2時は22時から翌日5時の範囲内に含まれます。"));
    }

    @Test
    @DisplayName("24時間全体範囲 - 12時が0時から0時に含まれる")
    void shouldReturnTrueForFullDayTimeRange() throws Exception {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(12)
                                                 .startHour(0)
                                                 .endHour(0)
                                                 .build();

      // When & Then
      mockMvc.perform(post("/api/v1/time-check")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(request)))
             .andDo(print())
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.data.isIncluded").value(true))
             .andExpect(jsonPath("$.data.rangeType").value("FULL_DAY"))
             .andExpect(jsonPath("$.data.description").value("対象時間12時は24時間全体の範囲内に含まれます。"));
    }

    @Test
    @DisplayName("範囲外ケース - 8時が9時から17時に含まれない")
    void shouldReturnFalseWhenOutOfRange() throws Exception {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(8)
                                                 .startHour(9)
                                                 .endHour(17)
                                                 .build();

      // When & Then
      mockMvc.perform(post("/api/v1/time-check")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(request)))
             .andDo(print())
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.data.isIncluded").value(false))
             .andExpect(jsonPath("$.data.rangeType").value("NORMAL"))
             .andExpect(jsonPath("$.data.description").value("対象時間8時は9時から17時の範囲内に含まれません。"));
    }
  }

  @Nested
  @DisplayName("バリデーションエラーテスト")
  class ValidationErrorCases {

    @Test
    @DisplayName("対象時間がnullの場合400エラー")
    void shouldReturnBadRequestWhenTargetHourIsNull() throws Exception {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(null)
                                                 .startHour(9)
                                                 .endHour(17)
                                                 .build();

      // When & Then
      mockMvc.perform(post("/api/v1/time-check")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(request)))
             .andDo(print())
             .andExpect(status().isBadRequest())
             .andExpect(jsonPath("$.code").value(400))
             .andExpect(jsonPath("$.message").value("対象時間は必須です。"));
    }

    @Test
    @DisplayName("時間が範囲外の場合400エラー - 負数")
    void shouldReturnBadRequestWhenHourIsNegative() throws Exception {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(-1)
                                                 .startHour(9)
                                                 .endHour(17)
                                                 .build();

      // When & Then
      mockMvc.perform(post("/api/v1/time-check")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(request)))
             .andDo(print())
             .andExpect(status().isBadRequest())
             .andExpect(jsonPath("$.code").value(400))
             .andExpect(jsonPath("$.message").value("時間は0から23の間で入力してください。"));
    }

    @Test
    @DisplayName("時間が範囲外の場合400エラー - 24以上")
    void shouldReturnBadRequestWhenHourIsGreaterThan23() throws Exception {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(24)
                                                 .startHour(9)
                                                 .endHour(17)
                                                 .build();

      // When & Then
      mockMvc.perform(post("/api/v1/time-check")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(request)))
             .andDo(print())
             .andExpect(status().isBadRequest())
             .andExpect(jsonPath("$.code").value(400))
             .andExpect(jsonPath("$.message").value("時間は0から23の間で入力してください。"));
    }

    @Test
    @DisplayName("無効なJSONフォーマットの場合400エラー")
    void shouldReturnBadRequestWhenInvalidJsonFormat() throws Exception {
      // Given
      String invalidJson = "{ invalid json }";

      // When & Then
      mockMvc.perform(post("/api/v1/time-check")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(invalidJson))
             .andDo(print())
             .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Content-Typeが不正な場合415エラー")
    void shouldReturnUnsupportedMediaTypeWhenWrongContentType() throws Exception {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(14)
                                                 .startHour(9)
                                                 .endHour(17)
                                                 .build();

      // When & Then
      mockMvc.perform(post("/api/v1/time-check")
                 .contentType(MediaType.TEXT_PLAIN)
                 .content(objectMapper.writeValueAsString(request)))
             .andDo(print())
             .andExpect(status().isUnsupportedMediaType());
    }
  }

  @Nested
  @DisplayName("境界値テスト")
  class BoundaryValueCases {

    @Test
    @DisplayName("境界値 - 開始時刻が含まれる")
    void shouldIncludeStartTime() throws Exception {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(9)
                                                 .startHour(9)
                                                 .endHour(17)
                                                 .build();

      // When & Then
      mockMvc.perform(post("/api/v1/time-check")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(request)))
             .andDo(print())
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.data.isIncluded").value(true));
    }

    @Test
    @DisplayName("境界値 - 終了時刻が含まれない")
    void shouldNotIncludeEndTime() throws Exception {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(17)
                                                 .startHour(9)
                                                 .endHour(17)
                                                 .build();

      // When & Then
      mockMvc.perform(post("/api/v1/time-check")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(request)))
             .andDo(print())
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.data.isIncluded").value(false));
    }

    @Test
    @DisplayName("境界値 - 0時と23時")
    void shouldHandleMidnightAndElevenPM() throws Exception {
      // Given - 0時が23時~1時の範囲に含まれる
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(0)
                                                 .startHour(23)
                                                 .endHour(1)
                                                 .build();

      // When & Then
      mockMvc.perform(post("/api/v1/time-check")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(request)))
             .andDo(print())
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.data.isIncluded").value(true))
             .andExpect(jsonPath("$.data.rangeType").value("MIDNIGHT_CROSSING"));
    }
  }

  @Nested
  @DisplayName("レスポンス形式テスト")
  class ResponseFormatTest {

    @Test
    @DisplayName("成功応答の構造確認")
    void shouldReturnCorrectResponseStructure() throws Exception {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(14)
                                                 .startHour(9)
                                                 .endHour(17)
                                                 .build();

      // When & Then
      mockMvc.perform(post("/api/v1/time-check")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(request)))
             .andDo(print())
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.code").exists())
             .andExpect(jsonPath("$.message").exists())
             .andExpect(jsonPath("$.data").exists())
             .andExpect(jsonPath("$.data.targetHour").exists())
             .andExpect(jsonPath("$.data.startHour").exists())
             .andExpect(jsonPath("$.data.endHour").exists())
             .andExpect(jsonPath("$.data.isIncluded").exists())
             .andExpect(jsonPath("$.data.rangeType").exists())
             .andExpect(jsonPath("$.data.description").exists());
    }

    @Test
    @DisplayName("日本語メッセージの確認")
    void shouldReturnJapaneseMessages() throws Exception {
      // Given
      TimeCheckRequest request = TimeCheckRequest.builder()
                                                 .targetHour(14)
                                                 .startHour(9)
                                                 .endHour(17)
                                                 .build();

      // When & Then
      mockMvc.perform(post("/api/v1/time-check")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(request)))
             .andDo(print())
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.message").value("リクエストは正常に処理されました。"))
             .andExpect(jsonPath("$.data.description").value("対象時間14時は9時から17時の範囲内に含まれます。"));
    }
  }
} 