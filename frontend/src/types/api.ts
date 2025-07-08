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

// 統計API用の型定義
export interface StatsResponse {
  success: boolean
  message: string
  data?: {
    totalChecks: number
    rangeStats: RangeStatsDto[]
    dailyStats: DailyStatsDto[]
  }
}

export interface RangeStatsDto {
  rangeType: 'NORMAL' | 'OVERNIGHT'
  inRangeCount: number
  outOfRangeCount: number
  totalCount: number
  inRangePercentage: number
}

export interface DailyStatsDto {
  date: string
  totalChecks: number
  inRangeCount: number
  outOfRangeCount: number
  inRangePercentage: number
}

// エラーレスポンス用の型定義
export interface ErrorResponse {
  success: false
  message: string
  errorCode?: string
} 