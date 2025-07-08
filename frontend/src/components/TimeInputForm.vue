<template>
  <div class="time-input-form">
    <h2 class="form-title">時間範囲チェック</h2>
    
    <form @submit.prevent="handleSubmit" class="form-container">
      <div class="input-group">
        <label for="startTime" class="input-label">開始時間</label>
        <input
          id="startTime"
          v-model="timeCheckStore.startTime"
          type="time"
          class="time-input"
          :disabled="timeCheckStore.isLoading"
          placeholder="09:00"
        />
      </div>

      <div class="input-group">
        <label for="endTime" class="input-label">終了時間</label>
        <input
          id="endTime"
          v-model="timeCheckStore.endTime"
          type="time"
          class="time-input"
          :disabled="timeCheckStore.isLoading"
          placeholder="18:00"
        />
      </div>

      <div class="button-group">
        <button
          type="submit"
          class="check-button"
          :disabled="!timeCheckStore.hasValidInput || timeCheckStore.isLoading"
        >
          <span v-if="timeCheckStore.isLoading" class="loading-spinner"></span>
          {{ timeCheckStore.isLoading ? 'チェック中...' : '時間をチェック' }}
        </button>
        
        <button
          type="button"
          class="clear-button"
          @click="timeCheckStore.clearForm"
          :disabled="timeCheckStore.isLoading"
        >
          クリア
        </button>
      </div>
    </form>

    <!-- エラーメッセージ表示 -->
    <div v-if="timeCheckStore.error" class="error-message">
      <span class="error-icon">⚠️</span>
      {{ timeCheckStore.error }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { useTimeCheckStore } from '@/stores/timeCheck'

const timeCheckStore = useTimeCheckStore()

const handleSubmit = async () => {
  await timeCheckStore.checkTimeRange()
}
</script>

<style scoped>
.time-input-form {
  max-width: 500px;
  margin: 0 auto;
  padding: 2rem;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.form-title {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 2rem;
  font-size: 1.8rem;
  font-weight: 600;
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
}

.time-input {
  padding: 0.75rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.2s ease;
  background: #ffffff;
}

.time-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.time-input:disabled {
  background-color: #f8fafc;
  cursor: not-allowed;
  opacity: 0.7;
}

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
  padding: 0.875rem 1.5rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.check-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #2563eb, #1e40af);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.check-button:disabled {
  background: #94a3b8;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.clear-button {
  flex: 1;
  background: #f8fafc;
  color: #64748b;
  border: 2px solid #e2e8f0;
  padding: 0.875rem 1rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.clear-button:hover:not(:disabled) {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #475569;
}

.clear-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top: 2px solid #ffffff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.error-message {
  margin-top: 1rem;
  padding: 0.75rem;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 8px;
  color: #dc2626;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
}

.error-icon {
  flex-shrink: 0;
}

/* レスポンシブデザイン */
@media (max-width: 640px) {
  .time-input-form {
    margin: 1rem;
    padding: 1.5rem;
  }
  
  .button-group {
    flex-direction: column;
  }
  
  .check-button {
    order: 1;
  }
  
  .clear-button {
    order: 2;
  }
}
</style> 