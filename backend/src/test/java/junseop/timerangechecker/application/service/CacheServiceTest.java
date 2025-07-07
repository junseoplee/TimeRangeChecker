package junseop.timerangechecker.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;
import junseop.timerangechecker.application.dto.TimeCheckResponse;
import junseop.timerangechecker.application.dto.stats.StatsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
@DisplayName("Redis キャッシュサービステスト")
class CacheServiceTest {

  @Mock
  private RedisTemplate<String, Object> redisTemplate;

  @Mock
  private ValueOperations<String, Object> valueOperations;

  @InjectMocks
  private CacheService cacheService;

  @BeforeEach
  void setUp() {
    lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
  }

  @Nested
  @DisplayName("時間チェック結果キャッシュテスト")
  class TimeCheckCacheTest {

    @Test
    @DisplayName("時間チェック結果をキャッシュに正常保存")
    void cacheTimeCheckResult_정상케이스() {
      // Given
      Integer targetHour = 14;
      Integer startHour = 9;
      Integer endHour = 17;
      TimeCheckResponse response = TimeCheckResponse.of(targetHour, startHour, endHour, true, "NORMAL");

      // When
      cacheService.cacheTimeCheckResult(targetHour, startHour, endHour, response);

      // Then
      verify(valueOperations).set(
          eq("timecheck:14:9:17"),
          eq(response),
          eq(1800L), // 30分
          eq(TimeUnit.SECONDS)
      );
    }

    @Test
    @DisplayName("キャッシュから時間チェック結果を正常取得")
    void getCachedTimeCheckResult_정상케이스() {
      // Given
      Integer targetHour = 14;
      Integer startHour = 9;
      Integer endHour = 17;
      TimeCheckResponse expectedResponse = TimeCheckResponse.of(targetHour, startHour, endHour, true, "NORMAL");
      
      when(valueOperations.get("timecheck:14:9:17")).thenReturn(expectedResponse);

      // When
      TimeCheckResponse result = cacheService.getCachedTimeCheckResult(targetHour, startHour, endHour);

      // Then
      assertThat(result).isNotNull();
      assertThat(result.getTargetHour()).isEqualTo(targetHour);
      assertThat(result.getIsIncluded()).isTrue();
      assertThat(result.getRangeType()).isEqualTo("NORMAL");
    }

    @Test
    @DisplayName("キャッシュヒットなし - nullを返す")
    void getCachedTimeCheckResult_キャッシュミス() {
      // Given
      when(valueOperations.get(anyString())).thenReturn(null);

      // When
      TimeCheckResponse result = cacheService.getCachedTimeCheckResult(14, 9, 17);

      // Then
      assertThat(result).isNull();
    }

    @Test
    @DisplayName("深夜跨ぎ時間範囲キャッシュテスト")
    void cacheTimeCheckResult_深夜跨ぎ() {
      // Given
      Integer targetHour = 2;
      Integer startHour = 22;
      Integer endHour = 5;
      TimeCheckResponse response = TimeCheckResponse.of(targetHour, startHour, endHour, true, "MIDNIGHT_CROSSING");

      // When
      cacheService.cacheTimeCheckResult(targetHour, startHour, endHour, response);

      // Then
      verify(valueOperations).set(
          eq("timecheck:2:22:5"),
          eq(response),
          eq(1800L),
          eq(TimeUnit.SECONDS)
      );
    }
  }

  @Nested
  @DisplayName("統計データキャッシュテスト")
  class StatsDataCacheTest {

    @Test
    @DisplayName("統計データをキャッシュに正常保存")
    void cacheStatsData_정상케이스() {
      // Given
      String statsType = "overall";
      StatsResponse statsData = StatsResponse.builder()
                                             .totalRequests(100L)
                                             .uniqueSessions(50L)
                                             .build();

      // When
      cacheService.cacheStatsData(statsType, statsData);

      // Then
      verify(valueOperations).set(
          eq("stats:overall"),
          eq(statsData),
          eq(600L), // 10分
          eq(TimeUnit.SECONDS)
      );
    }

    @Test
    @DisplayName("キャッシュから統計データを正常取得")
    void getCachedStatsData_정상케이스() {
      // Given
      String statsType = "overall";
      StatsResponse expectedStats = StatsResponse.builder()
                                                  .totalRequests(100L)
                                                  .uniqueSessions(50L)
                                                  .build();
      
      when(valueOperations.get("stats:overall")).thenReturn(expectedStats);

      // When
      StatsResponse result = cacheService.getCachedStatsData(statsType, StatsResponse.class);

      // Then
      assertThat(result).isNotNull();
      assertThat(result.getTotalRequests()).isEqualTo(100L);
      assertThat(result.getUniqueSessions()).isEqualTo(50L);
    }

    @Test
    @DisplayName("統計データキャッシュヒットなし")
    void getCachedStatsData_キャッシュミス() {
      // Given
      when(valueOperations.get(anyString())).thenReturn(null);

      // When
      StatsResponse result = cacheService.getCachedStatsData("overall", StatsResponse.class);

      // Then
      assertThat(result).isNull();
    }

    @Test
    @DisplayName("型が一致しない場合nullを返す")
    void getCachedStatsData_타입불일치() {
      // Given
      when(valueOperations.get("stats:overall")).thenReturn("wrong_type_data");

      // When
      StatsResponse result = cacheService.getCachedStatsData("overall", StatsResponse.class);

      // Then
      assertThat(result).isNull();
    }
  }

  @Nested
  @DisplayName("キャッシュ無効化テスト")
  class CacheInvalidationTest {

    @Test
    @DisplayName("統計キャッシュ無効化 - キーが存在する場合")
    void invalidateStatsCache_キー존재() {
      // Given
      java.util.Set<String> mockKeys = java.util.Set.of("stats:overall", "stats:daily");
      when(redisTemplate.keys("stats:*")).thenReturn(mockKeys);

      // When
      cacheService.invalidateStatsCache();

      // Then
      verify(redisTemplate).keys("stats:*");
      verify(redisTemplate).delete(mockKeys);
    }

    @Test
    @DisplayName("統計キャッシュ無効化 - キーが存在しない場合")
    void invalidateStatsCache_キー미존재() {
      // Given
      when(redisTemplate.keys("stats:*")).thenReturn(java.util.Set.of());

      // When
      cacheService.invalidateStatsCache();

      // Then
      verify(redisTemplate).keys("stats:*");
      // delete는 호출되지 않아야 함
    }
  }

  @Nested
  @DisplayName("キャッシュキー生成テスト")
  class CacheKeyGenerationTest {

    @Test
    @DisplayName("時間チェックキャッシュキー생성확인")
    void timeCheckCacheKey_생성확인() {
      // Given
      TimeCheckResponse response = TimeCheckResponse.of(0, 23, 1, true, "MIDNIGHT_CROSSING");

      // When
      cacheService.cacheTimeCheckResult(0, 23, 1, response);

      // Then
      verify(valueOperations).set(
          eq("timecheck:0:23:1"),
          any(),
          anyLong(),
          any(TimeUnit.class)
      );
    }

    @Test
    @DisplayName("統計データキャッシュキー생성확인")
    void statsDataCacheKey_생성확인() {
      // Given
      StatsResponse stats = StatsResponse.builder().build();

      // When
      cacheService.cacheStatsData("daily", stats);

      // Then
      verify(valueOperations).set(
          eq("stats:daily"),
          any(),
          anyLong(),
          any(TimeUnit.class)
      );
    }
  }
} 