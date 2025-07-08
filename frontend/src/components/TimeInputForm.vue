<template>
  <div class="time-input-form">
    <h2 class="form-title">時間範囲チェック</h2>
    
    <!-- 入力方式選択タブ -->
    <div class="input-mode-tabs">
      <button 
        @click="inputMode = 'traditional'"
        class="tab-button"
        :class="{ active: inputMode === 'traditional' }"
      >
        📝 従来入力
      </button>
      <button 
        @click="inputMode = 'clock'"
        class="tab-button"
        :class="{ active: inputMode === 'clock' }"
      >
        🕐 時計選択
      </button>
    </div>

    <!-- ローディングオーバーレイ -->
    <div v-if="timeCheckStore.isLoading" class="loading-overlay">
      <div class="loading-content">
        <div class="enhanced-spinner">
          <div class="spinner-ring ring-1"></div>
          <div class="spinner-ring ring-2"></div>
          <div class="spinner-ring ring-3"></div>
          <div class="spinner-center">
            <span class="spinner-icon">⏰</span>
          </div>
        </div>
        <div class="loading-text">
          <span class="loading-message">時間をチェック中</span>
          <div class="loading-dots">
            <span class="dot dot-1">.</span>
            <span class="dot dot-2">.</span>
            <span class="dot dot-3">.</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 従来の入力方式 -->
    <div v-if="inputMode === 'traditional'" class="traditional-input">
      <form @submit.prevent="handleSubmit" class="form-container">
        <div class="input-group">
          <label for="startTime" class="input-label">開始時間</label>
          <div class="input-wrapper">
            <input
              id="startTime"
              v-model="timeCheckStore.startTime"
              type="time"
              class="time-input"
              :disabled="timeCheckStore.isLoading"
              placeholder="09:00"
            />
            <div class="input-focus-ring"></div>
          </div>
        </div>

        <div class="input-group">
          <label for="endTime" class="input-label">終了時間</label>
          <div class="input-wrapper">
            <input
              id="endTime"
              v-model="timeCheckStore.endTime"
              type="time"
              class="time-input"
              :disabled="timeCheckStore.isLoading"
              placeholder="18:00"
            />
            <div class="input-focus-ring"></div>
          </div>
        </div>

        <div class="button-group">
          <button
            type="submit"
            class="check-button"
            :disabled="!timeCheckStore.hasValidInput || timeCheckStore.isLoading"
            @click="handleButtonClick($event)"
          >
            <span class="button-content">
              <span v-if="timeCheckStore.isLoading" class="loading-spinner"></span>
              <span class="button-text">{{ timeCheckStore.isLoading ? 'チェック中...' : '時間をチェック' }}</span>
            </span>
            <div class="button-ripple"></div>
            <div class="button-shine"></div>
          </button>
          
          <button
            type="button"
            class="clear-button"
            @click="handleClearClick"
            :disabled="timeCheckStore.isLoading"
          >
            <span class="button-content">
              <span class="button-text">クリア</span>
            </span>
            <div class="button-ripple"></div>
          </button>
        </div>
      </form>
    </div>

    <!-- 時計式入力方式 -->
    <div v-else-if="inputMode === 'clock'" class="clock-input">
      <ClockTimePicker 
        v-model:startTime="timeCheckStore.startTime"
        v-model:endTime="timeCheckStore.endTime"
      />
      
      <div class="clock-actions">
        <button
          @click="handleButtonClick($event)"
          class="check-button"
          :disabled="!timeCheckStore.hasValidInput || timeCheckStore.isLoading"
        >
          <span class="button-content">
            <span v-if="timeCheckStore.isLoading" class="loading-spinner"></span>
            <span class="button-text">{{ timeCheckStore.isLoading ? 'チェック中...' : '時間をチェック' }}</span>
          </span>
          <div class="button-ripple"></div>
          <div class="button-shine"></div>
        </button>
        
        <button
          @click="handleClearClick"
          class="clear-button"
          :disabled="timeCheckStore.isLoading"
        >
          <span class="button-content">
            <span class="button-text">クリア</span>
          </span>
          <div class="button-ripple"></div>
        </button>
      </div>
    </div>

    <!-- エラーメッセージ表示 -->
    <div v-if="timeCheckStore.error" class="error-message">
      <span class="error-icon">⚠️</span>
      <span class="error-text">{{ timeCheckStore.error }}</span>
      <div class="error-pulse"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useTimeCheckStore } from '@/stores/timeCheck'
import ClockTimePicker from './ClockTimePicker.vue'

const timeCheckStore = useTimeCheckStore()
const inputMode = ref<'traditional' | 'clock'>('clock') // デフォルトは時計モード

const handleSubmit = async () => {
  await timeCheckStore.checkTimeRange()
}

const handleButtonClick = (event: MouseEvent) => {
  const button = event.currentTarget as HTMLButtonElement
  const ripple = button.querySelector('.button-ripple') as HTMLElement
  
  if (ripple) {
    const rect = button.getBoundingClientRect()
    const size = Math.max(rect.width, rect.height)
    const x = event.clientX - rect.left - size / 2
    const y = event.clientY - rect.top - size / 2
    
    ripple.style.width = size + 'px'
    ripple.style.height = size + 'px'
    ripple.style.left = x + 'px'
    ripple.style.top = y + 'px'
    
    ripple.classList.remove('ripple-active')
    ripple.offsetHeight // Force reflow
    ripple.classList.add('ripple-active')
  }
  
  handleSubmit()
}

const handleClearClick = (event: MouseEvent) => {
  const button = event.currentTarget as HTMLButtonElement
  const ripple = button.querySelector('.button-ripple') as HTMLElement
  
  if (ripple) {
    const rect = button.getBoundingClientRect()
    const size = Math.max(rect.width, rect.height)
    const x = event.clientX - rect.left - size / 2
    const y = event.clientY - rect.top - size / 2
    
    ripple.style.width = size + 'px'
    ripple.style.height = size + 'px'
    ripple.style.left = x + 'px'
    ripple.style.top = y + 'px'
    
    ripple.classList.remove('ripple-active')
    ripple.offsetHeight // Force reflow
    ripple.classList.add('ripple-active')
  }
  
  timeCheckStore.clearForm()
}
</script>

<style scoped>
.time-input-form {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  position: relative;
}

.form-title {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 2rem;
  font-size: 1.8rem;
  font-weight: 600;
}

/* ローディングオーバーレイ */
.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  border-radius: 12px;
  animation: fadeIn 0.3s ease-out;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.5rem;
}

.enhanced-spinner {
  position: relative;
  width: 80px;
  height: 80px;
}

.spinner-ring {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 3px solid transparent;
}

.ring-1 {
  border-top-color: #3b82f6;
  animation: spin 1s linear infinite;
}

.ring-2 {
  border-right-color: #8b5cf6;
  animation: spin 1.2s linear infinite reverse;
  width: 65%;
  height: 65%;
  top: 17.5%;
  left: 17.5%;
}

.ring-3 {
  border-bottom-color: #10b981;
  animation: spin 0.8s linear infinite;
  width: 35%;
  height: 35%;
  top: 32.5%;
  left: 32.5%;
}

.spinner-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 1.5rem;
  animation: pulse 2s ease-in-out infinite;
}

.loading-text {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #64748b;
  font-weight: 500;
}

.loading-dots {
  display: flex;
  gap: 0.1rem;
}

.dot {
  animation: dotBounce 1.4s infinite ease-in-out both;
  font-size: 1.2rem;
  color: #3b82f6;
}

.dot-1 { animation-delay: -0.32s; }
.dot-2 { animation-delay: -0.16s; }
.dot-3 { animation-delay: 0s; }

@keyframes dotBounce {
  0%, 80%, 100% { 
    transform: scale(0);
    opacity: 0.5;
  } 
  40% { 
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes pulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); }
  50% { transform: translate(-50%, -50%) scale(1.1); }
}

/* 入力方式選択タブ */
.input-mode-tabs {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 2rem;
  background: #f1f5f9;
  padding: 0.25rem;
  border-radius: 12px;
}

.tab-button {
  flex: 1;
  padding: 0.75rem 1rem;
  background: transparent;
  border: none;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  position: relative;
  overflow: hidden;
}

.tab-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
  transition: left 0.5s ease;
}

.tab-button:hover::before {
  left: 100%;
}

.tab-button:hover {
  background: #e2e8f0;
  color: #475569;
  transform: translateY(-1px);
}

.tab-button.active {
  background: #ffffff;
  color: #1e293b;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

/* 従来入力方式 */
.traditional-input {
  animation: fadeIn 0.3s ease-in-out;
}

.form-container {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.input-label {
  font-weight: 500;
  color: #555;
  font-size: 0.95rem;
  transition: color 0.2s ease;
}

.input-wrapper {
  position: relative;
}

.time-input {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: #ffffff;
  position: relative;
  z-index: 1;
}

.time-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  transform: translateY(-1px);
}

.time-input:focus + .input-focus-ring {
  transform: scale(1);
  opacity: 1;
}

.input-focus-ring {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: 2px solid #3b82f6;
  border-radius: 8px;
  transform: scale(0.95);
  opacity: 0;
  transition: all 0.3s ease;
  pointer-events: none;
}

.time-input:disabled {
  background-color: #f8fafc;
  cursor: not-allowed;
  opacity: 0.7;
}

/* 時計式入力方式 */
.clock-input {
  animation: fadeIn 0.3s ease-in-out;
}

.clock-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
  justify-content: center;
}

/* 共通ボタンスタイル */
.button-group {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.check-button {
  flex: 2;
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  color: white;
  border: none;
  padding: 1rem 1.5rem;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  min-width: 140px;
  position: relative;
  overflow: hidden;
}

.check-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #2563eb, #1e40af);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.4);
}

.check-button:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.check-button:disabled {
  background: linear-gradient(135deg, #94a3b8, #64748b);
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.clear-button {
  flex: 1;
  background: #f8fafc;
  color: #64748b;
  border: 2px solid #e2e8f0;
  padding: 1rem 1rem;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  min-width: 80px;
  position: relative;
  overflow: hidden;
}

.clear-button:hover:not(:disabled) {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #475569;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.clear-button:active:not(:disabled) {
  transform: translateY(0);
}

.clear-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.button-content {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.button-text {
  transition: all 0.2s ease;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top: 2px solid #ffffff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.button-ripple {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: scale(0);
  pointer-events: none;
  z-index: 1;
}

.button-ripple.ripple-active {
  animation: rippleEffect 0.6s ease-out;
}

.button-shine {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
  transition: left 0.5s ease;
  z-index: 1;
}

.check-button:hover .button-shine,
.clear-button:hover .button-shine {
  left: 100%;
}

.error-message {
  margin-top: 1rem;
  padding: 1rem;
  background: #fef2f2;
  border: 2px solid #fecaca;
  border-radius: 12px;
  color: #dc2626;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 0.9rem;
  position: relative;
  overflow: hidden;
  animation: errorSlideIn 0.3s ease-out;
}

.error-icon {
  flex-shrink: 0;
  font-size: 1.2rem;
  animation: errorShake 0.5s ease-in-out;
}

.error-text {
  flex: 1;
  font-weight: 500;
}

.error-pulse {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent, rgba(239, 68, 68, 0.1), transparent);
  animation: errorPulse 2s ease-in-out infinite;
}

/* アニメーション定義 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes rippleEffect {
  0% {
    transform: scale(0);
    opacity: 1;
  }
  100% {
    transform: scale(4);
    opacity: 0;
  }
}

@keyframes errorSlideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes errorShake {
  0%, 100% { transform: translateX(0); }
  10%, 30%, 50%, 70%, 90% { transform: translateX(-2px); }
  20%, 40%, 60%, 80% { transform: translateX(2px); }
}

@keyframes errorPulse {
  0%, 100% { transform: translateX(-100%); }
  50% { transform: translateX(100%); }
}

/* レスポンシブデザイン */
@media (max-width: 640px) {
  .time-input-form {
    margin: 1rem;
    padding: 1.5rem;
  }
  
  .input-mode-tabs {
    flex-direction: column;
    gap: 0.25rem;
  }
  
  .tab-button {
    padding: 0.6rem 1rem;
  }
  
  .button-group,
  .clock-actions {
    flex-direction: column;
  }
  
  .check-button {
    order: 1;
  }
  
  .clear-button {
    order: 2;
  }

  .enhanced-spinner {
    width: 60px;
    height: 60px;
  }

  .loading-text {
    font-size: 0.9rem;
  }
}
</style> 