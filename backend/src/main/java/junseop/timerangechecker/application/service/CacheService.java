package junseop.timerangechecker.application.service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import junseop.timerangechecker.application.dto.TimeCheckResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Redis キャッシュサービス
 * 時間チェック結果と統計データの効率的なキャッシュ管理を提供
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

  private final RedisTemplate<String, Object> redisTemplate;

  // キャッシュキープレフィックス
  private static final String TIME_CHECK_CACHE_PREFIX = "timecheck:";
  private static final String STATS_CACHE_PREFIX = "stats:";
  
  // キャッシュ有効期限設定
  private static final Duration TIME_CHECK_CACHE_TTL = Duration.ofMinutes(30);  // 時間チェック結果: 30分
  private static final Duration STATS_CACHE_TTL = Duration.ofMinutes(10);       // 統計データ: 10分

  /**
   * 時間チェック結果をキャッシュに保存
   * 
   * @param targetHour 対象時間
   * @param startHour 開始時間  
   * @param endHour 終了時間
   * @param response 時間チェック結果
   */
  public void cacheTimeCheckResult(Integer targetHour, Integer startHour, Integer endHour, TimeCheckResponse response) {
    try {
      String cacheKey = generateTimeCheckCacheKey(targetHour, startHour, endHour);
      
      redisTemplate.opsForValue().set(
          cacheKey, 
          response, 
          TIME_CHECK_CACHE_TTL.toSeconds(), 
          TimeUnit.SECONDS
      );
      
      log.debug("時間チェック結果をキャッシュに保存: key={}, response={}", cacheKey, response);
      
    } catch (Exception e) {
      log.warn("時間チェック結果キャッシュ保存失敗: targetHour={}, startHour={}, endHour={}", 
               targetHour, startHour, endHour, e);
    }
  }

  /**
   * キャッシュから時間チェック結果を取得
   * 
   * @param targetHour 対象時間
   * @param startHour 開始時間
   * @param endHour 終了時間
   * @return キャッシュされた時間チェック結果（存在しない場合はnull）
   */
  public TimeCheckResponse getCachedTimeCheckResult(Integer targetHour, Integer startHour, Integer endHour) {
    try {
      String cacheKey = generateTimeCheckCacheKey(targetHour, startHour, endHour);
      
      Object cachedResult = redisTemplate.opsForValue().get(cacheKey);
      
      if (cachedResult instanceof TimeCheckResponse response) {
        log.debug("時間チェック結果をキャッシュから取得: key={}", cacheKey);
        return response;
      }
      
      log.debug("時間チェック結果キャッシュヒットなし: key={}", cacheKey);
      return null;
      
    } catch (Exception e) {
      log.warn("時間チェック結果キャッシュ取得失敗: targetHour={}, startHour={}, endHour={}", 
               targetHour, startHour, endHour, e);
      return null;
    }
  }

  /**
   * 統計データをキャッシュに保存
   * 
   * @param statsType 統計タイプ（例: "overall", "daily"）
   * @param data 統計データ
   */
  public void cacheStatsData(String statsType, Object data) {
    try {
      String cacheKey = generateStatsCacheKey(statsType);
      
      redisTemplate.opsForValue().set(
          cacheKey, 
          data, 
          STATS_CACHE_TTL.toSeconds(), 
          TimeUnit.SECONDS
      );
      
      log.debug("統計データをキャッシュに保存: key={}", cacheKey);
      
    } catch (Exception e) {
      log.warn("統計データキャッシュ保存失敗: statsType={}", statsType, e);
    }
  }

  /**
   * キャッシュから統計データを取得
   * 
   * @param statsType 統計タイプ
   * @param clazz 期待される戻り値のクラス
   * @return キャッシュされた統計データ（存在しない場合はnull）
   */
  @SuppressWarnings("unchecked")
  public <T> T getCachedStatsData(String statsType, Class<T> clazz) {
    try {
      String cacheKey = generateStatsCacheKey(statsType);
      
      Object cachedData = redisTemplate.opsForValue().get(cacheKey);
      
      if (cachedData != null && clazz.isInstance(cachedData)) {
        log.debug("統計データをキャッシュから取得: key={}", cacheKey);
        return (T) cachedData;
      }
      
      log.debug("統計データキャッシュヒットなし: key={}", cacheKey);
      return null;
      
    } catch (Exception e) {
      log.warn("統計データキャッシュ取得失敗: statsType={}", statsType, e);
      return null;
    }
  }

  /**
   * 統計関連キャッシュを無効化
   * 新しいデータが追加されたときに呼び出される
   */
  public void invalidateStatsCache() {
    try {
      String pattern = STATS_CACHE_PREFIX + "*";
      var keys = redisTemplate.keys(pattern);
      
      if (keys != null && !keys.isEmpty()) {
        redisTemplate.delete(keys);
        log.debug("統計キャッシュを無効化しました: 削除キー数={}", keys.size());
      }
      
    } catch (Exception e) {
      log.warn("統計キャッシュ無効化失敗", e);
    }
  }

  /**
   * 時間チェック用キャッシュキー生成
   */
  private String generateTimeCheckCacheKey(Integer targetHour, Integer startHour, Integer endHour) {
    return TIME_CHECK_CACHE_PREFIX + targetHour + ":" + startHour + ":" + endHour;
  }

  /**
   * 統計用キャッシュキー生成
   */
  private String generateStatsCacheKey(String statsType) {
    return STATS_CACHE_PREFIX + statsType;
  }
} 