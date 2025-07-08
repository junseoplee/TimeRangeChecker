import type { 
  TimeCheckRequest, 
  TimeCheckResponse, 
  StatsResponse,
  ErrorResponse 
} from '@/types/api'

// 環境変数からAPIベースURLを取得
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// バックエンドAPI要求形式
interface BackendTimeCheckRequest {
  targetHour: number
  startHour: number
  endHour: number
}

// バックエンド実際応答形式 (successの代わりにcode使用)
interface BackendTimeCheckResponse {
  code: number
  message: string
  data?: {
    targetHour: number
    startHour: number
    endHour: number
    isIncluded: boolean
    rangeType: string
    description: string
  }
}

class ApiService {
  private async request<T>(
    endpoint: string, 
    options: RequestInit = {}
  ): Promise<T> {
    const url = `${API_BASE_URL}${endpoint}`
    
    const config: RequestInit = {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    }

    try {
      const response = await fetch(url, config)
      
      if (!response.ok) {
        const errorData: ErrorResponse = await response.json()
        throw new Error(errorData.message || `HTTP エラー: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      if (error instanceof Error) {
        throw error
      }
      throw new Error('ネットワークエラーが発生しました')
    }
  }

  // 時間文字列を整数に変換 (例: "09:30" -> 9)
  private timeStringToHour(timeString: string): number {
    const [hour] = timeString.split(':')
    return parseInt(hour, 10)
  }

  // 現在時間取得
  private getCurrentHour(): number {
    return new Date().getHours()
  }

  // 時間チェックAPI呼び出し
  async checkTimeRange(request: TimeCheckRequest): Promise<TimeCheckResponse> {
    const backendRequest: BackendTimeCheckRequest = {
      targetHour: this.getCurrentHour(),
      startHour: this.timeStringToHour(request.startTime),
      endHour: this.timeStringToHour(request.endTime)
    }

    const backendResponse = await this.request<BackendTimeCheckResponse>('/api/v1/time-check', {
      method: 'POST',
      body: JSON.stringify(backendRequest),
    })

    // バックエンド応答をフロントエンド形式に変換
    const currentTime = new Date().toLocaleTimeString('ja-JP', { 
      hour: '2-digit', 
      minute: '2-digit', 
      second: '2-digit' 
    })

    // codeが200なら成功として処理
    const isSuccess = backendResponse.code === 200

    return {
      success: isSuccess,
      message: backendResponse.message,
      data: backendResponse.data ? {
        isInRange: backendResponse.data.isIncluded,
        currentTime: currentTime,
        startTime: request.startTime,
        endTime: request.endTime,
        rangeType: backendResponse.data.rangeType === 'MIDNIGHT_CROSSING' ? 'OVERNIGHT' : 'NORMAL'
      } : undefined
    }
  }

  // 統計情報取得API呼び出し
  async getStatistics(): Promise<StatsResponse> {
    return this.request<StatsResponse>('/api/v1/statistics/overall')
  }
}

export const apiService = new ApiService()
export default apiService 