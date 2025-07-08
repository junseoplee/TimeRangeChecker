import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { apiService } from '@/services/api'
import type { TimeCheckRequest, TimeCheckResponse } from '@/types/api'

export const useTimeCheckStore = defineStore('timeCheck', () => {
  // 状態管理
  const isLoading = ref(false)
  const error = ref<string | null>(null)
  const lastCheckResult = ref<TimeCheckResponse | null>(null)
  
  // フォーム状態
  const startTime = ref('')
  const endTime = ref('')
  
  // 計算されたプロパティ
  const hasResult = computed(() => lastCheckResult.value !== null)
  const isInRange = computed(() => lastCheckResult.value?.data?.isInRange ?? false)
  const hasValidInput = computed(() => {
    return startTime.value.length > 0 && endTime.value.length > 0
  })

  // アクション
  const checkTimeRange = async () => {
    if (!hasValidInput.value) {
      error.value = '開始時間と終了時間を入力してください'
      return
    }

    isLoading.value = true
    error.value = null

    try {
      const request: TimeCheckRequest = {
        startTime: startTime.value,
        endTime: endTime.value
      }

      const response = await apiService.checkTimeRange(request)
      
      if (response.success) {
        lastCheckResult.value = response
      } else {
        error.value = response.message || '時間チェックに失敗しました'
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : '予期しないエラーが発生しました'
    } finally {
      isLoading.value = false
    }
  }

  const clearResult = () => {
    lastCheckResult.value = null
    error.value = null
  }

  const clearForm = () => {
    startTime.value = ''
    endTime.value = ''
    clearResult()
  }

  const setTimeRange = (start: string, end: string) => {
    startTime.value = start
    endTime.value = end
  }

  return {
    // 状態
    isLoading,
    error,
    lastCheckResult,
    startTime,
    endTime,
    
    // 計算されたプロパティ
    hasResult,
    isInRange,
    hasValidInput,
    
    // アクション
    checkTimeRange,
    clearResult,
    clearForm,
    setTimeRange
  }
}) 