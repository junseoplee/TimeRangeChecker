<template>
  <div v-if="timeCheckStore.hasResult" class="result-container">
    <div class="result-header">
      <h3 class="result-title">チェック結果</h3>
      <button @click="timeCheckStore.clearResult" class="close-button">
        ✕
      </button>
    </div>

    <div class="result-content">
      <!-- メイン結果表示 -->
      <div class="main-result" :class="{ 'in-range': timeCheckStore.isInRange, 'out-of-range': !timeCheckStore.isInRange }">
        <div class="result-icon">
          {{ timeCheckStore.isInRange ? '✅' : '❌' }}
        </div>
        <div class="result-text">
          <h4 class="result-status">
            {{ timeCheckStore.isInRange ? '時間範囲内です' : '時間範囲外です' }}
          </h4>
          <p class="result-description">
            {{ timeCheckStore.isInRange 
              ? '現在時刻は指定された時間範囲に含まれています' 
              : '現在時刻は指定された時間範囲に含まれていません' 
            }}
          </p>
        </div>
      </div>

      <!-- 詳細情報 -->
      <div class="details-section">
        <h5 class="details-title">詳細情報</h5>
        <div class="details-grid">
          <div class="detail-item">
            <span class="detail-label">現在時刻</span>
            <span class="detail-value">{{ formatTime(timeCheckStore.lastCheckResult?.data?.currentTime) }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">開始時間</span>
            <span class="detail-value">{{ timeCheckStore.lastCheckResult?.data?.startTime }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">終了時間</span>
            <span class="detail-value">{{ timeCheckStore.lastCheckResult?.data?.endTime }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">範囲タイプ</span>
            <span class="detail-value">
              {{ timeCheckStore.lastCheckResult?.data?.rangeType === 'OVERNIGHT' ? '日跨ぎ' : '通常' }}
            </span>
          </div>
        </div>
      </div>

      <!-- アクションボタン -->
      <div class="action-buttons">
        <button @click="timeCheckStore.checkTimeRange" class="recheck-button">
          再チェック
        </button>
        <button @click="timeCheckStore.clearForm" class="new-check-button">
          新しい時間で確認
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useTimeCheckStore } from '@/stores/timeCheck'

const timeCheckStore = useTimeCheckStore()

// 時間フォーマット関数
const formatTime = (timeString?: string) => {
  if (!timeString) return '---'
  
  try {
    const date = new Date(timeString)
    return date.toLocaleTimeString('ja-JP', { 
      hour: '2-digit', 
      minute: '2-digit', 
      second: '2-digit' 
    })
  } catch {
    return timeString
  }
}
</script>

<style scoped>
.result-container {
  max-width: 500px;
  margin: 2rem auto 0;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 2rem 1rem;
  border-bottom: 1px solid #e2e8f0;
}

.result-title {
  color: #2c3e50;
  font-size: 1.3rem;
  font-weight: 600;
  margin: 0;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.2rem;
  color: #64748b;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.close-button:hover {
  background-color: #f1f5f9;
  color: #475569;
}

.result-content {
  padding: 1.5rem 2rem 2rem;
}

.main-result {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.5rem;
  border-radius: 12px;
  margin-bottom: 1.5rem;
  transition: all 0.3s ease;
}

.main-result.in-range {
  background: linear-gradient(135deg, #dcfce7, #bbf7d0);
  border: 2px solid #22c55e;
}

.main-result.out-of-range {
  background: linear-gradient(135deg, #fef2f2, #fecaca);
  border: 2px solid #ef4444;
}

.result-icon {
  font-size: 2.5rem;
  flex-shrink: 0;
}

.result-text {
  flex: 1;
}

.result-status {
  margin: 0 0 0.5rem 0;
  font-size: 1.3rem;
  font-weight: 600;
}

.main-result.in-range .result-status {
  color: #15803d;
}

.main-result.out-of-range .result-status {
  color: #dc2626;
}

.result-description {
  margin: 0;
  color: #64748b;
  font-size: 0.95rem;
  line-height: 1.5;
}

.details-section {
  margin-bottom: 1.5rem;
}

.details-title {
  color: #374151;
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0 0 1rem 0;
}

.details-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  padding: 0.75rem;
  background: #f8fafc;
  border-radius: 8px;
}

.detail-label {
  font-size: 0.8rem;
  font-weight: 500;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.detail-value {
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
}

.action-buttons {
  display: flex;
  gap: 1rem;
}

.recheck-button {
  flex: 1;
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  color: white;
  border: none;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.recheck-button:hover {
  background: linear-gradient(135deg, #2563eb, #1e40af);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.new-check-button {
  flex: 1;
  background: #f8fafc;
  color: #64748b;
  border: 2px solid #e2e8f0;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.new-check-button:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #475569;
}

/* レスポンシブデザイン */
@media (max-width: 640px) {
  .result-container {
    margin: 1rem;
  }
  
  .result-header {
    padding: 1rem 1.5rem 0.75rem;
  }
  
  .result-content {
    padding: 1rem 1.5rem 1.5rem;
  }
  
  .main-result {
    flex-direction: column;
    text-align: center;
    gap: 0.75rem;
  }
  
  .details-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style> 