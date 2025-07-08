import type { 
  TimeCheckRequest, 
  TimeCheckResponse, 
  StatsResponse,
  ErrorResponse 
} from '@/types/api'

// 환境변数からAPIベースURLを取得
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

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

  // 時間チェックAPI呼び出し
  async checkTimeRange(request: TimeCheckRequest): Promise<TimeCheckResponse> {
    return this.request<TimeCheckResponse>('/api/time-check', {
      method: 'POST',
      body: JSON.stringify(request),
    })
  }

  // 統計情報取得API呼び出し
  async getStatistics(): Promise<StatsResponse> {
    return this.request<StatsResponse>('/api/statistics')
  }
}

export const apiService = new ApiService()
export default apiService 