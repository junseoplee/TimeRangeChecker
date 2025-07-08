// 時間チェックAPI用の型定義
export interface TimeCheckRequest {
  startTime: string // HH:mm 形式
  endTime: string   // HH:mm 形式
}

export interface TimeCheckResponse {
  success: boolean
  message: string
  data?: {
    isInRange: boolean
    currentTime: string
    startTime: string
    endTime: string
    rangeType: 'NORMAL' | 'OVERNIGHT'
  }
}

// 統計API用の型定義 - バックエンドレスポンス構造に合わせて修正
export interface StatsResponse {
  success: boolean
  message: string
  data?: {
    totalRequests: number
    uniqueSessions: number
    popularRanges: PopularRangeDto[]
    dailyStats: DailyStatsDto[]
  }
}

export interface PopularRangeDto {
  rangeKey: string
  requestCount: number
  matchCount: number
  matchRate: number
  lastAccessed: string
}

export interface DailyStatsDto {
  date: string
  totalRequests: number
  uniqueSessions: number
  popularRange: string
}

// エラーレスポンス用の型定義
export interface ErrorResponse {
  success: false
  message: string
  errorCode?: string
} 