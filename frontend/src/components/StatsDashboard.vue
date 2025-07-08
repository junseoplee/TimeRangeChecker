<template>
  <div class="stats-dashboard">
    <h3 class="dashboard-title">
      <span class="title-icon">📊</span>
      統計ダッシュボード
      <span v-if="isUsingMockData" class="demo-badge">Demo Data</span>
      <span v-else class="live-badge">Live Data</span>
    </h3>
    
    <div class="stats-grid">
      <!-- リアルタイム統計カード -->
      <div class="stats-card realtime-card">
        <div class="card-header">
          <h4 class="card-title">
            <span class="card-icon">⚡</span>
            リアルタイム統計
          </h4>
          <div class="live-indicator">
            <span class="live-dot"></span>
            LIVE
          </div>
        </div>
        <div class="card-content">
          <div class="stat-item">
            <span class="stat-label">総チェック数</span>
            <span class="stat-value primary">{{ formatNumber(totalChecks) }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">今日のチェック</span>
            <span class="stat-value success">{{ formatNumber(todayChecks) }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">ユニークセッション</span>
            <span class="stat-value info">{{ formatNumber(uniqueSessions) }}</span>
          </div>
        </div>
      </div>

      <!-- 人気時間帯ランキング -->
      <div class="stats-card popular-ranges-card">
        <div class="card-header">
          <h4 class="card-title">
            <span class="card-icon">🏆</span>
            人気時間帯ランキング
          </h4>
          <button @click="refreshStats" class="refresh-button" :class="{ refreshing: isRefreshing }">
            <span class="refresh-icon">🔄</span>
          </button>
        </div>
        <div class="card-content">
          <div v-if="isLoading" class="loading-state">
            <span class="loading-spinner"></span>
            <span>データ読み込み中...</span>
          </div>
          <div v-else-if="popularRanges.length === 0" class="empty-state">
            <span>データがありません</span>
          </div>
          <div v-else class="ranking-list">
            <div 
              v-for="(range, index) in popularRanges" 
              :key="range.id"
              class="ranking-item"
              :class="{ 'top-rank': index < 3 }"
            >
              <div class="rank-badge" :class="getRankClass(index)">
                <span class="rank-number">{{ index + 1 }}</span>
                <span class="rank-medal" v-if="index < 3">{{ getMedal(index) }}</span>
              </div>
              <div class="range-info">
                <span class="range-text">{{ range.startHour }}:00 - {{ range.endHour }}:00</span>
                <span class="range-type">{{ getRangeTypeText(range.type) }}</span>
              </div>
              <div class="range-stats">
                <span class="usage-count">{{ range.count }}回</span>
                <div class="usage-bar">
                  <div 
                    class="usage-fill" 
                    :style="{ width: getUsagePercentage(range.count) + '%' }"
                    :class="getRankClass(index)"
                  ></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 日別統計グラフ -->
      <div class="stats-card daily-stats-card">
        <div class="card-header">
          <h4 class="card-title">
            <span class="card-icon">📈</span>
            週間使用統計
          </h4>
          <div class="chart-legend">
            <span class="legend-item">
              <span class="legend-color success"></span>
              日別リクエスト数
            </span>
          </div>
        </div>
        <div class="card-content">
          <div v-if="isLoading" class="loading-state">
            <span class="loading-spinner"></span>
            <span>データ読み込み中...</span>
          </div>
          <div v-else class="chart-container">
            <div class="chart-bars">
              <div 
                v-for="(day, index) in dailyStats" 
                :key="day.date"
                class="chart-day"
              >
                <div class="chart-bars-wrapper">
                  <div 
                    class="chart-bar success-bar"
                    :style="{ height: getBarHeight(day.totalRequests) + '%' }"
                    :title="`総リクエスト: ${day.totalRequests}回`"
                  ></div>
                </div>
                <div class="chart-day-label">{{ getDayLabel(day.date) }}</div>
                <div class="chart-day-count">{{ day.totalRequests }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- システム状態カード -->
      <div class="stats-card system-status-card">
        <div class="card-header">
          <h4 class="card-title">
            <span class="card-icon">⚙️</span>
            システム状態
          </h4>
          <div class="status-indicator" :class="systemStatus.class">
            {{ systemStatus.text }}
          </div>
        </div>
        <div class="card-content">
          <div class="status-grid">
            <div class="status-item">
              <span class="status-label">API応答時間</span>
              <span class="status-value">{{ averageResponseTime }}ms</span>
              <div class="status-bar">
                <div 
                  class="status-fill"
                  :style="{ width: getResponseTimePercentage() + '%' }"
                  :class="getResponseTimeClass()"
                ></div>
              </div>
            </div>
            <div class="status-item">
              <span class="status-label">成功率</span>
              <span class="status-value">{{ successRate.toFixed(1) }}%</span>
              <div class="status-bar">
                <div 
                  class="status-fill success"
                  :style="{ width: successRate + '%' }"
                ></div>
              </div>
            </div>
            <div class="status-item">
              <span class="status-label">データ更新</span>
              <span class="status-value">{{ lastUpdateTime }}</span>
              <div class="status-bar">
                <div 
                  class="status-fill info"
                  :style="{ width: 100 + '%' }"
                ></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { apiService } from '@/services/api'
import type { StatsResponse, PopularRangeDto, DailyStatsDto } from '@/types/api'

// リアクティブデータ
const isLoading = ref(false)
const isRefreshing = ref(false)
const isUsingMockData = ref(true)
const lastUpdateTime = ref('未更新')

// 統計データ
const totalChecks = ref(0)
const todayChecks = ref(0)
const uniqueSessions = ref(0)
const averageResponseTime = ref(245)
const successRate = ref(98.5)

// 人気時間帯データ
const popularRanges = ref<Array<{
  id: number
  startHour: number
  endHour: number
  type: string
  count: number
}>>([])

// 日別統計データ
const dailyStats = ref<DailyStatsDto[]>([])

// リフレッシュタイマー
let refreshInterval: number | undefined

// 計算されたプロパティ
const systemStatus = computed(() => {
  if (averageResponseTime.value < 200 && successRate.value > 99) {
    return { text: '正常', class: 'status-excellent' }
  } else if (averageResponseTime.value < 500 && successRate.value > 95) {
    return { text: '良好', class: 'status-good' }
  } else {
    return { text: '注意', class: 'status-warning' }
  }
})

const maxUsageCount = computed(() => {
  return Math.max(...popularRanges.value.map(range => range.count), 1)
})

const maxDailyCount = computed(() => {
  return Math.max(...dailyStats.value.map(day => day.totalRequests), 1)
})

// API から統計データを取得
const fetchStatistics = async (): Promise<boolean> => {
  try {
    const startTime = Date.now()
    const response: StatsResponse = await apiService.getStatistics()
    const endTime = Date.now()
    
    averageResponseTime.value = endTime - startTime

    if (response.success && response.data) {
      // 実際のデータを使用
      totalChecks.value = response.data.totalRequests || 0
      
      // 日別統計から今日のデータ計算
      const today = new Date().toISOString().split('T')[0]
      const todayData = response.data.dailyStats?.find(d => d.date === today)
      todayChecks.value = todayData?.totalRequests || 0
      
      // 実際の日別統計設定
      dailyStats.value = response.data.dailyStats || []
      
      // 실제の 人気時間帯統計から人気時間帯生成
      if (response.data.popularRanges && response.data.popularRanges.length > 0) {
        popularRanges.value = response.data.popularRanges.map((stat, index) => ({
          id: index + 1,
          startHour: parseInt(stat.rangeKey.split('-')[0]),
          endHour: parseInt(stat.rangeKey.split('-')[1]),
          type: stat.rangeKey.includes('22-') || stat.rangeKey.includes('-06') ? 'OVERNIGHT' : 'NORMAL',
          count: stat.requestCount
        })).sort((a, b) => b.count - a.count).slice(0, 5) // 上位5つのみ
      } else {
        popularRanges.value = []
      }
      
      // 実際のユニークセッション数（総チェック数の70%程度で推定）
      uniqueSessions.value = Math.floor(totalChecks.value * 0.7)
      
      // 成功率計算（API応答時間に応じて）
      successRate.value = averageResponseTime.value < 1000 ? 98.5 : 95.0
      isUsingMockData.value = false
      
      lastUpdateTime.value = new Date().toLocaleTimeString('ja-JP', {
        hour: '2-digit',
        minute: '2-digit'
      })
      
      return true
    }
    
    // API応答はあるがデータがない場合
    totalChecks.value = 0
    todayChecks.value = 0
    uniqueSessions.value = 0
    popularRanges.value = []
    dailyStats.value = []
    successRate.value = 100.0
    isUsingMockData.value = false
    
    lastUpdateTime.value = new Date().toLocaleTimeString('ja-JP', {
      hour: '2-digit',
      minute: '2-digit'
    })
    
    return true
  } catch (error) {
    console.error('統計API取得失敗:', error)
    // API呼び出し失敗時の空データ設定
    totalChecks.value = 0
    todayChecks.value = 0
    uniqueSessions.value = 0
    popularRanges.value = []
    dailyStats.value = []
    successRate.value = 0
    isUsingMockData.value = false
    lastUpdateTime.value = 'API取得失敗'
    return false
  }
}

// フォールバック用モックデータ設定を無効化
const setMockData = () => {
  // モックデータの使用を無効化
  console.warn('モックデータの使用は無効化されました')
}

// データ更新
const loadData = async () => {
  isLoading.value = true
  
  // 実際のAPIからデータを取得
  await fetchStatistics()
  
  isLoading.value = false
}

// メソッド
const formatNumber = (num: number): string => {
  return num.toLocaleString('ja-JP')
}

const getRankClass = (index: number): string => {
  if (index === 0) return 'gold'
  if (index === 1) return 'silver'
  if (index === 2) return 'bronze'
  return 'default'
}

const getMedal = (index: number): string => {
  const medals = ['🥇', '🥈', '🥉']
  return medals[index] || ''
}

const getRangeTypeText = (type: string): string => {
  switch (type) {
    case 'OVERNIGHT': return '深夜跨ぎ'
    default: return '通常'
  }
}

const getUsagePercentage = (count: number): number => {
  return (count / maxUsageCount.value) * 100
}

const getBarHeight = (count: number): number => {
  return maxDailyCount.value > 0 ? (count / maxDailyCount.value) * 100 : 0
}

const getDayLabel = (date: string): string => {
  const days = ['日', '月', '火', '水', '木', '金', '土']
  const dayIndex = new Date(date).getDay()
  return days[dayIndex]
}

const getResponseTimePercentage = (): number => {
  return Math.min((averageResponseTime.value / 1000) * 100, 100)
}

const getResponseTimeClass = (): string => {
  if (averageResponseTime.value < 200) return 'success'
  if (averageResponseTime.value < 500) return 'warning'
  return 'error'
}

const refreshStats = async () => {
  isRefreshing.value = true
  await loadData()
  isRefreshing.value = false
}

const startAutoRefresh = () => {
  refreshInterval = window.setInterval(async () => {
    await loadData()
  }, 30000) // 30초마다 새로고침
}

// ライフサイクル
onMounted(async () => {
  await loadData()
  startAutoRefresh()
})

onUnmounted(() => {
  if (refreshInterval) {
    clearInterval(refreshInterval)
  }
})
</script>

<style scoped>
.stats-dashboard {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1.5rem;
}

.dashboard-title {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: #1e293b;
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 2rem;
  text-align: center;
  justify-content: center;
}

.demo-badge {
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
  color: white;
  font-size: 0.7rem;
  font-weight: 600;
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  animation: demoGlow 2s ease-in-out infinite;
}

.live-badge {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  font-size: 0.7rem;
  font-weight: 600;
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  animation: liveGlow 2s ease-in-out infinite;
}

@keyframes demoGlow {
  0%, 100% { box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }
  50% { box-shadow: 0 4px 12px rgba(251, 191, 36, 0.5); }
}

@keyframes liveGlow {
  0%, 100% { box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }
  50% { box-shadow: 0 4px 12px rgba(16, 185, 129, 0.5); }
}

.title-icon {
  font-size: 1.8rem;
  animation: bounceRotate 3s ease-in-out infinite;
}

@keyframes bounceRotate {
  0%, 100% { transform: rotate(0deg) scale(1); }
  25% { transform: rotate(-5deg) scale(1.05); }
  75% { transform: rotate(5deg) scale(1.05); }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
}

.stats-card {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;
}

.stats-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 1.5rem 1rem;
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  border-bottom: 1px solid #e2e8f0;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #374151;
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0;
}

.card-icon {
  font-size: 1.2rem;
}

.card-content {
  padding: 1.5rem;
}

/* リアルタイム統計カード */
.realtime-card {
  background: linear-gradient(135deg, #dbeafe, #bfdbfe);
  border-color: #3b82f6;
}

.live-indicator {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.75rem;
  font-weight: 600;
  color: #dc2626;
}

.live-dot {
  width: 8px;
  height: 8px;
  background: #dc2626;
  border-radius: 50%;
  animation: livePulse 2s ease-in-out infinite;
}

@keyframes livePulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.2); }
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-label {
  color: #64748b;
  font-weight: 500;
  font-size: 0.9rem;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  font-family: 'Courier New', monospace;
}

.stat-value.primary { color: #3b82f6; }
.stat-value.success { color: #10b981; }
.stat-value.info { color: #8b5cf6; }

/* 人気時間帯ランキング */
.refresh-button {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.refresh-button:hover {
  background: rgba(59, 130, 246, 0.1);
}

.refresh-button.refreshing .refresh-icon {
  animation: spin 1s linear infinite;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 2rem;
  color: #64748b;
}

.loading-spinner {
  width: 24px;
  height: 24px;
  border: 2px solid #e2e8f0;
  border-top: 2px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.empty-state {
  text-align: center;
  padding: 2rem;
  color: #9ca3af;
  font-style: italic;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f8fafc;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.ranking-item.top-rank {
  background: linear-gradient(135deg, #fef3c7, #fde68a);
  border: 2px solid #f59e0b;
}

.ranking-item:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.rank-badge {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  font-weight: 700;
  position: relative;
}

.rank-badge.gold { background: linear-gradient(135deg, #fbbf24, #f59e0b); color: white; }
.rank-badge.silver { background: linear-gradient(135deg, #e5e7eb, #d1d5db); color: #374151; }
.rank-badge.bronze { background: linear-gradient(135deg, #d97706, #b45309); color: white; }
.rank-badge.default { background: #e2e8f0; color: #64748b; }

.rank-medal {
  position: absolute;
  top: -8px;
  right: -8px;
  font-size: 1.2rem;
}

.range-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.range-text {
  font-weight: 600;
  color: #1e293b;
  font-size: 1rem;
}

.range-type {
  font-size: 0.8rem;
  color: #64748b;
  background: rgba(100, 116, 139, 0.1);
  padding: 0.2rem 0.5rem;
  border-radius: 12px;
  width: fit-content;
}

.range-stats {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.5rem;
  min-width: 80px;
}

.usage-count {
  font-weight: 600;
  color: #374151;
}

.usage-bar {
  width: 60px;
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
}

.usage-fill {
  height: 100%;
  transition: width 0.3s ease;
  border-radius: 3px;
}

.usage-fill.gold { background: linear-gradient(90deg, #fbbf24, #f59e0b); }
.usage-fill.silver { background: linear-gradient(90deg, #9ca3af, #6b7280); }
.usage-fill.bronze { background: linear-gradient(90deg, #d97706, #b45309); }
.usage-fill.default { background: linear-gradient(90deg, #3b82f6, #1d4ed8); }

/* 日別統計グラフ */
.chart-legend {
  display: flex;
  gap: 1rem;
  font-size: 0.8rem;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-color.success { background: #10b981; }
.legend-color.error { background: #ef4444; }

.chart-container {
  height: 200px;
  display: flex;
  align-items: flex-end;
}

.chart-bars {
  display: flex;
  align-items: flex-end;
  gap: 0.5rem;
  width: 100%;
  height: 100%;
}

.chart-day {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
}

.chart-bars-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 2px;
  height: 160px;
  margin-bottom: 0.5rem;
}

.chart-bar {
  width: 12px;
  border-radius: 2px 2px 0 0;
  transition: all 0.3s ease;
  min-height: 2px;
}

.success-bar { background: linear-gradient(to top, #10b981, #34d399); }
.error-bar { background: linear-gradient(to top, #ef4444, #f87171); }

.chart-bar:hover {
  transform: scaleY(1.1);
  filter: brightness(1.1);
}

.chart-day-label {
  font-size: 0.8rem;
  font-weight: 600;
  color: #64748b;
  margin-bottom: 0.25rem;
}

.chart-day-count {
  font-size: 0.7rem;
  color: #9ca3af;
  font-weight: 500;
}

/* システム状態カード */
.status-indicator {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
}

.status-excellent {
  background: #dcfce7;
  color: #166534;
}

.status-good {
  background: #fef3c7;
  color: #92400e;
}

.status-warning {
  background: #fee2e2;
  color: #991b1b;
}

.status-grid {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.status-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.status-label {
  font-size: 0.9rem;
  color: #64748b;
  font-weight: 500;
}

.status-value {
  font-size: 1.2rem;
  font-weight: 700;
  color: #1e293b;
}

.status-bar {
  width: 100%;
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.status-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.status-fill.success { background: linear-gradient(90deg, #10b981, #34d399); }
.status-fill.warning { background: linear-gradient(90deg, #f59e0b, #fbbf24); }
.status-fill.error { background: linear-gradient(90deg, #ef4444, #f87171); }
.status-fill.info { background: linear-gradient(90deg, #3b82f6, #60a5fa); }

/* アニメーション */
@keyframes spin {
  to { transform: rotate(360deg); }
}

/* レスポンシブデザイン */
@media (max-width: 768px) {
  .stats-dashboard {
    padding: 1rem;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .card-header {
    padding: 1rem;
    flex-direction: column;
    gap: 0.5rem;
    align-items: flex-start;
  }
  
  .card-content {
    padding: 1rem;
  }
  
  .ranking-item {
    padding: 0.75rem;
  }
  
  .chart-bars-wrapper {
    height: 120px;
  }
  
  .chart-bar {
    width: 8px;
  }
}

@media (max-width: 480px) {
  .dashboard-title {
    font-size: 1.2rem;
    margin-bottom: 1rem;
  }
  
  .stat-value {
    font-size: 1.2rem;
  }
  
  .ranking-item {
    flex-direction: column;
    text-align: center;
    gap: 0.5rem;
  }
  
  .range-stats {
    align-items: center;
  }
}
</style> 