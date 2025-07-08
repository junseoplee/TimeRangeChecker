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
        <div class="result-icon-container">
          <div class="result-icon" :class="{ 'success-pulse': timeCheckStore.isInRange, 'error-shake': !timeCheckStore.isInRange }">
            {{ timeCheckStore.isInRange ? '✅' : '❌' }}
          </div>
          <div class="icon-glow" :class="{ 'success-glow': timeCheckStore.isInRange, 'error-glow': !timeCheckStore.isInRange }"></div>
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
          <div class="detail-item" data-label="current">
            <span class="detail-label">現在時刻</span>
            <span class="detail-value">{{ formatTime(timeCheckStore.lastCheckResult?.data?.currentTime) }}</span>
            <div class="detail-indicator current-time-indicator"></div>
          </div>
          <div class="detail-item" data-label="start">
            <span class="detail-label">開始時間</span>
            <span class="detail-value">{{ timeCheckStore.lastCheckResult?.data?.startTime }}</span>
            <div class="detail-indicator start-time-indicator"></div>
          </div>
          <div class="detail-item" data-label="end">
            <span class="detail-label">終了時間</span>
            <span class="detail-value">{{ timeCheckStore.lastCheckResult?.data?.endTime }}</span>
            <div class="detail-indicator end-time-indicator"></div>
          </div>
          <div class="detail-item" data-label="type">
            <span class="detail-label">範囲タイプ</span>
            <span class="detail-value range-type-badge" :class="{ 'overnight-badge': timeCheckStore.lastCheckResult?.data?.rangeType === 'OVERNIGHT' }">
              {{ timeCheckStore.lastCheckResult?.data?.rangeType === 'OVERNIGHT' ? '🌙 日跨ぎ' : '☀️ 通常' }}
            </span>
            <div class="detail-indicator type-indicator"></div>
          </div>
        </div>
      </div>

      <!-- アクションボタン -->
      <div class="action-buttons">
        <button @click="timeCheckStore.checkTimeRange" class="recheck-button" :class="{ 'loading': timeCheckStore.isLoading }">
          <span v-if="timeCheckStore.isLoading" class="button-spinner"></span>
          <span class="button-text">{{ timeCheckStore.isLoading ? 'チェック中...' : '再チェック' }}</span>
          <div class="button-glow"></div>
        </button>
        <button @click="timeCheckStore.clearForm" class="new-check-button">
          <span class="button-text">新しい時間で確認</span>
          <div class="button-ripple"></div>
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
  border-radius: 16px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  animation: slideInFromBottom 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  position: relative;
}

/* 改善されたアニメーション */
@keyframes slideInFromBottom {
  0% {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  60% {
    opacity: 0.8;
    transform: translateY(-5px) scale(1.02);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 2rem 1rem;
  border-bottom: 1px solid #e2e8f0;
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
}

.result-title {
  color: #2c3e50;
  font-size: 1.4rem;
  font-weight: 700;
  margin: 0;
  animation: fadeInUp 0.3s ease-out 0.2s both;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.3rem;
  color: #64748b;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 50%;
  transition: all 0.2s ease;
  position: relative;
  overflow: hidden;
}

.close-button:hover {
  background-color: #fee2e2;
  color: #dc2626;
  transform: rotate(90deg) scale(1.1);
}

.close-button:active {
  transform: rotate(90deg) scale(0.95);
}

.result-content {
  padding: 1.5rem 2rem 2rem;
}

.main-result {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  padding: 2rem;
  border-radius: 16px;
  margin-bottom: 2rem;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  position: relative;
  overflow: hidden;
}

.main-result::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
  transition: left 0.5s ease;
}

.main-result:hover::before {
  left: 100%;
}

.main-result.in-range {
  background: linear-gradient(135deg, #dcfce7, #bbf7d0);
  border: 2px solid #22c55e;
  box-shadow: 0 8px 25px rgba(34, 197, 94, 0.2);
}

.main-result.out-of-range {
  background: linear-gradient(135deg, #fef2f2, #fecaca);
  border: 2px solid #ef4444;
  box-shadow: 0 8px 25px rgba(239, 68, 68, 0.2);
}

.result-icon-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.result-icon {
  font-size: 3rem;
  flex-shrink: 0;
  position: relative;
  z-index: 2;
  animation: fadeInUp 0.5s ease-out 0.3s both;
}

.icon-glow {
  position: absolute;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  z-index: 1;
}

.success-pulse {
  animation: successPulse 2s ease-in-out infinite;
}

.error-shake {
  animation: errorShake 0.5s ease-in-out;
}

.success-glow {
  background: radial-gradient(circle, rgba(34, 197, 94, 0.3) 0%, transparent 70%);
  animation: glowPulse 2s ease-in-out infinite;
}

.error-glow {
  background: radial-gradient(circle, rgba(239, 68, 68, 0.3) 0%, transparent 70%);
  animation: glowPulse 1s ease-in-out 3;
}

@keyframes successPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

@keyframes errorShake {
  0%, 100% { transform: translateX(0); }
  10%, 30%, 50%, 70%, 90% { transform: translateX(-3px); }
  20%, 40%, 60%, 80% { transform: translateX(3px); }
}

@keyframes glowPulse {
  0%, 100% { opacity: 0.5; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.2); }
}

.result-text {
  flex: 1;
  animation: fadeInUp 0.5s ease-out 0.4s both;
}

.result-status {
  margin: 0 0 0.75rem 0;
  font-size: 1.5rem;
  font-weight: 700;
  animation: fadeInUp 0.3s ease-out 0.5s both;
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
  font-size: 1rem;
  line-height: 1.6;
  animation: fadeInUp 0.3s ease-out 0.6s both;
}

.details-section {
  margin-bottom: 2rem;
  animation: fadeInUp 0.5s ease-out 0.7s both;
}

.details-title {
  color: #374151;
  font-size: 1.2rem;
  font-weight: 600;
  margin: 0 0 1.5rem 0;
}

.details-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 1rem;
  background: #f8fafc;
  border-radius: 12px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.detail-item:hover {
  background: #f1f5f9;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.detail-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(to bottom, #3b82f6, #1d4ed8);
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.detail-item:hover::before {
  transform: scaleY(1);
}

.detail-indicator {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.detail-item:hover .detail-indicator {
  opacity: 1;
}

.current-time-indicator {
  background: #3b82f6;
  animation: blink 2s ease-in-out infinite;
}

.start-time-indicator {
  background: #10b981;
}

.end-time-indicator {
  background: #ef4444;
}

.type-indicator {
  background: #8b5cf6;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

.detail-label {
  font-size: 0.85rem;
  font-weight: 600;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.detail-value {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1e293b;
}

.range-type-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.9rem !important;
  font-weight: 600 !important;
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
  color: white !important;
}

.range-type-badge.overnight-badge {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
}

.action-buttons {
  display: flex;
  gap: 1rem;
  animation: fadeInUp 0.5s ease-out 0.8s both;
}

.recheck-button {
  flex: 2;
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  color: white;
  border: none;
  padding: 1rem 1.5rem;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  position: relative;
  overflow: hidden;
}

.recheck-button:hover:not(.loading) {
  background: linear-gradient(135deg, #2563eb, #1e40af);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.4);
}

.recheck-button.loading {
  cursor: not-allowed;
  background: linear-gradient(135deg, #94a3b8, #64748b);
}

.button-spinner {
  width: 18px;
  height: 18px;
  border: 2px solid transparent;
  border-top: 2px solid #ffffff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.button-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, transparent 30%, rgba(255,255,255,0.3) 50%, transparent 70%);
  transform: translateX(-100%);
  transition: transform 0.6s ease;
}

.recheck-button:hover .button-glow {
  transform: translateX(100%);
}

.new-check-button {
  flex: 1;
  background: #f8fafc;
  color: #64748b;
  border: 2px solid #e2e8f0;
  padding: 1rem 1rem;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.new-check-button:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #475569;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.button-ripple {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(59, 130, 246, 0.2);
  transform: translate(-50%, -50%);
  transition: width 0.4s ease, height 0.4s ease;
}

.new-check-button:active .button-ripple {
  width: 200px;
  height: 200px;
}

.button-text {
  position: relative;
  z-index: 2;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
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
    gap: 1rem;
    padding: 1.5rem;
  }
  
  .details-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style> 