<template>
  <div class="clock-time-picker">
    <h3 class="clock-title">アナログ時計で時間選択</h3>
    
    <div class="clock-container">
      <svg 
        class="clock-svg" 
        viewBox="0 0 400 400" 
        @mousedown="handleMouseDown"
        @mousemove="handleMouseMove"
        @mouseup="handleMouseUp"
        @touchstart="handleTouchStart"
        @touchmove="handleTouchMove"
        @touchend="handleTouchEnd"
      >
        <!-- 시계 외곽 원 -->
        <circle cx="200" cy="200" r="190" class="clock-border"/>
        <circle cx="200" cy="200" r="180" class="clock-face"/>
        
        <!-- 시간 숫자 표시 -->
        <g class="hour-numbers">
          <text 
            v-for="hour in 24" 
            :key="hour"
            :x="getHourPosition(hour - 1).x" 
            :y="getHourPosition(hour - 1).y"
            class="hour-text"
            :class="{ 'hour-text-major': (hour - 1) % 6 === 0 }"
          >
            {{ hour - 1 }}
          </text>
        </g>
        
        <!-- 시간 마커 -->
        <g class="hour-markers">
          <line 
            v-for="hour in 24" 
            :key="hour"
            :x1="getMarkerPosition(hour - 1, 165).x"
            :y1="getMarkerPosition(hour - 1, 165).y"
            :x2="getMarkerPosition(hour - 1, hour % 6 === 0 ? 140 : 155).x"
            :y2="getMarkerPosition(hour - 1, hour % 6 === 0 ? 140 : 155).y"
            :class="hour % 6 === 0 ? 'hour-marker-major' : 'hour-marker-minor'"
          />
        </g>
        
        <!-- 시간 범위 표시 (호) -->
        <path 
          v-if="hasValidRange"
          :d="getRangeArcPath()"
          class="time-range-arc"
          :class="{ 'overnight-range': isOvernightRange }"
        />
        
        <!-- 시작 시간 핸들 -->
        <g class="start-handle" v-if="startAngle !== null">
          <circle 
            :cx="getHandlePosition(startAngle).x" 
            :cy="getHandlePosition(startAngle).y" 
            r="12" 
            class="time-handle start-time-handle"
            :class="{ 'dragging': dragging === 'start' }"
          />
          <text 
            :x="getHandlePosition(startAngle).x" 
            :y="getHandlePosition(startAngle).y + 5" 
            class="handle-text"
          >
            開始
          </text>
        </g>
        
        <!-- 종료 시간 핸들 -->
        <g class="end-handle" v-if="endAngle !== null">
          <circle 
            :cx="getHandlePosition(endAngle).x" 
            :cy="getHandlePosition(endAngle).y" 
            r="12" 
            class="time-handle end-time-handle"
            :class="{ 'dragging': dragging === 'end' }"
          />
          <text 
            :x="getHandlePosition(endAngle).x" 
            :y="getHandlePosition(endAngle).y + 5" 
            class="handle-text"
          >
            終了
          </text>
        </g>
        
        <!-- 중앙 점 -->
        <circle cx="200" cy="200" r="4" class="clock-center"/>
      </svg>
      
      <!-- 선택된 시간 표시 -->
      <div class="selected-times">
        <div class="time-display">
          <span class="time-label">開始時間:</span>
          <span class="time-value">{{ formatTime(startHour) }}</span>
        </div>
        <div class="time-display">
          <span class="time-label">終了時間:</span>
          <span class="time-value">{{ formatTime(endHour) }}</span>
        </div>
        <div v-if="isOvernightRange" class="overnight-indicator">
          <span class="overnight-text">🌙 深夜を跨ぐ範囲</span>
        </div>
      </div>
    </div>
    
    <!-- 프리셋 버튼들 -->
    <div class="preset-buttons">
      <button 
        v-for="preset in timePresets" 
        :key="preset.name"
        @click="setPreset(preset)"
        class="preset-button"
        :class="{ active: isActivePreset(preset) }"
      >
        {{ preset.name }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

// Props
interface Props {
  startTime?: string
  endTime?: string
}

const props = withDefaults(defineProps<Props>(), {
  startTime: '',
  endTime: ''
})

// Emits
const emit = defineEmits<{
  'update:startTime': [value: string]
  'update:endTime': [value: string]
}>()

// Reactive data
const dragging = ref<'start' | 'end' | null>(null)
const startAngle = ref<number | null>(null)
const endAngle = ref<number | null>(null)

// プリセット時間範囲
const timePresets = [
  { name: '営業時間', start: 9, end: 17 },
  { name: '深夜勤務', start: 22, end: 6 },
  { name: '24時間', start: 0, end: 0 },
  { name: '午前中', start: 6, end: 12 },
  { name: '午後', start: 12, end: 18 }
]

// Computed
const startHour = computed(() => {
  if (startAngle.value === null) return null
  return Math.round(startAngle.value / 15) % 24
})

const endHour = computed(() => {
  if (endAngle.value === null) return null
  return Math.round(endAngle.value / 15) % 24
})

const hasValidRange = computed(() => {
  return startAngle.value !== null && endAngle.value !== null
})

const isOvernightRange = computed(() => {
  if (!hasValidRange.value) return false
  return startHour.value! > endHour.value! && !(startHour.value === endHour.value)
})

// Watch props changes
watch(() => props.startTime, (newVal) => {
  if (newVal && newVal !== formatTime(startHour.value)) {
    const hour = parseInt(newVal.split(':')[0])
    startAngle.value = (hour * 15) % 360
  }
}, { immediate: true })

watch(() => props.endTime, (newVal) => {
  if (newVal && newVal !== formatTime(endHour.value)) {
    const hour = parseInt(newVal.split(':')[0])
    endAngle.value = (hour * 15) % 360
  }
}, { immediate: true })

// Watch computed values and emit changes
watch(startHour, (newVal) => {
  if (newVal !== null) {
    emit('update:startTime', formatTime(newVal))
  }
})

watch(endHour, (newVal) => {
  if (newVal !== null) {
    emit('update:endTime', formatTime(newVal))
  }
})

// Methods
const getHourPosition = (hour: number) => {
  const angle = (hour * 15 - 90) * Math.PI / 180
  const radius = 120
  return {
    x: 200 + radius * Math.cos(angle),
    y: 200 + radius * Math.sin(angle)
  }
}

const getMarkerPosition = (hour: number, radius: number) => {
  const angle = (hour * 15 - 90) * Math.PI / 180
  return {
    x: 200 + radius * Math.cos(angle),
    y: 200 + radius * Math.sin(angle)
  }
}

const getHandlePosition = (angle: number) => {
  const radians = (angle - 90) * Math.PI / 180
  const radius = 150
  return {
    x: 200 + radius * Math.cos(radians),
    y: 200 + radius * Math.sin(radians)
  }
}

const getAngleFromPosition = (x: number, y: number): number => {
  const centerX = 200
  const centerY = 200
  const dx = x - centerX
  const dy = y - centerY
  let angle = Math.atan2(dy, dx) * 180 / Math.PI + 90
  if (angle < 0) angle += 360
  
  // 15도 단위로 스냅
  angle = Math.round(angle / 15) * 15
  return angle % 360
}

const getMousePosition = (event: MouseEvent | TouchEvent): { x: number, y: number } => {
  const svg = event.currentTarget as SVGElement
  const rect = svg.getBoundingClientRect()
  
  let clientX: number, clientY: number
  if (event instanceof MouseEvent) {
    clientX = event.clientX
    clientY = event.clientY
  } else {
    clientX = event.touches[0].clientX
    clientY = event.touches[0].clientY
  }
  
  const scaleX = 400 / rect.width
  const scaleY = 400 / rect.height
  
  return {
    x: (clientX - rect.left) * scaleX,
    y: (clientY - rect.top) * scaleY
  }
}

const getClosestHandle = (x: number, y: number): 'start' | 'end' | null => {
  if (startAngle.value === null && endAngle.value === null) return 'start'
  if (startAngle.value === null) return 'start'
  if (endAngle.value === null) return 'end'
  
  const startPos = getHandlePosition(startAngle.value)
  const endPos = getHandlePosition(endAngle.value)
  
  const startDist = Math.sqrt((x - startPos.x) ** 2 + (y - startPos.y) ** 2)
  const endDist = Math.sqrt((x - endPos.x) ** 2 + (y - endPos.y) ** 2)
  
  if (startDist < 30 && endDist < 30) {
    return startDist < endDist ? 'start' : 'end'
  } else if (startDist < 30) {
    return 'start'
  } else if (endDist < 30) {
    return 'end'
  }
  
  return null
}

const handleMouseDown = (event: MouseEvent) => {
  event.preventDefault()
  const pos = getMousePosition(event)
  const handle = getClosestHandle(pos.x, pos.y)
  
  if (handle) {
    dragging.value = handle
  } else {
    // クリックした位置に新しいハンドルを作成
    const angle = getAngleFromPosition(pos.x, pos.y)
    if (startAngle.value === null) {
      startAngle.value = angle
      dragging.value = 'start'
    } else if (endAngle.value === null) {
      endAngle.value = angle
      dragging.value = 'end'
    } else {
      // 両方設定済みの場合、より近いハンドルを更新
      const closestHandle = getClosestHandle(pos.x, pos.y) || 'start'
      if (closestHandle === 'start') {
        startAngle.value = angle
      } else {
        endAngle.value = angle
      }
      dragging.value = closestHandle
    }
  }
}

const handleMouseMove = (event: MouseEvent) => {
  if (!dragging.value) return
  event.preventDefault()
  
  const pos = getMousePosition(event)
  const angle = getAngleFromPosition(pos.x, pos.y)
  
  if (dragging.value === 'start') {
    startAngle.value = angle
  } else {
    endAngle.value = angle
  }
}

const handleMouseUp = () => {
  dragging.value = null
}

const handleTouchStart = (event: TouchEvent) => {
  event.preventDefault()
  const pos = getMousePosition(event)
  const handle = getClosestHandle(pos.x, pos.y)
  
  if (handle) {
    dragging.value = handle
  }
}

const handleTouchMove = (event: TouchEvent) => {
  if (!dragging.value) return
  event.preventDefault()
  
  const pos = getMousePosition(event)
  const angle = getAngleFromPosition(pos.x, pos.y)
  
  if (dragging.value === 'start') {
    startAngle.value = angle
  } else {
    endAngle.value = angle
  }
}

const handleTouchEnd = () => {
  dragging.value = null
}

const getRangeArcPath = (): string => {
  if (!hasValidRange.value) return ''
  
  const start = startAngle.value!
  const end = endAngle.value!
  const radius = 150
  
  const startRad = (start - 90) * Math.PI / 180
  const endRad = (end - 90) * Math.PI / 180
  
  const startX = 200 + radius * Math.cos(startRad)
  const startY = 200 + radius * Math.sin(startRad)
  const endX = 200 + radius * Math.cos(endRad)
  const endY = 200 + radius * Math.sin(endRad)
  
  let largeArcFlag: number
  if (isOvernightRange.value) {
    largeArcFlag = (start - end + 360) % 360 > 180 ? 0 : 1
  } else {
    largeArcFlag = (end - start + 360) % 360 > 180 ? 1 : 0
  }
  
  return `M ${startX} ${startY} A ${radius} ${radius} 0 ${largeArcFlag} 1 ${endX} ${endY}`
}

const formatTime = (hour: number | null): string => {
  if (hour === null) return '--:--'
  return `${hour.toString().padStart(2, '0')}:00`
}

const setPreset = (preset: typeof timePresets[0]) => {
  startAngle.value = (preset.start * 15) % 360
  endAngle.value = (preset.end * 15) % 360
}

const isActivePreset = (preset: typeof timePresets[0]): boolean => {
  return startHour.value === preset.start && endHour.value === preset.end
}
</script>

<style scoped>
.clock-time-picker {
  max-width: 600px;
  margin: 0 auto;
  padding: 1.5rem;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.clock-title {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 1.5rem;
  font-size: 1.3rem;
  font-weight: 600;
}

.clock-container {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.5rem;
}

.clock-svg {
  width: 100%;
  max-width: 400px;
  height: auto;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.clock-svg:hover {
  transform: scale(1.02);
}

.clock-border {
  fill: none;
  stroke: #cbd5e1;
  stroke-width: 3;
}

.clock-face {
  fill: #f8fafc;
  stroke: none;
}

.hour-text {
  fill: #64748b;
  font-size: 14px;
  font-weight: 500;
  text-anchor: middle;
  dominant-baseline: central;
  transition: fill 0.2s ease;
}

.hour-text-major {
  fill: #1e293b;
  font-size: 16px;
  font-weight: 600;
}

.hour-marker-major {
  stroke: #1e293b;
  stroke-width: 2;
}

.hour-marker-minor {
  stroke: #94a3b8;
  stroke-width: 1;
}

.time-range-arc {
  fill: none;
  stroke: #3b82f6;
  stroke-width: 12;
  stroke-linecap: round;
  opacity: 0.6;
  transition: all 0.3s ease;
}

.time-range-arc.overnight-range {
  stroke: #8b5cf6;
  stroke-dasharray: 8 4;
}

.time-handle {
  fill: #ffffff;
  stroke-width: 3;
  cursor: grab;
  transition: all 0.2s ease;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.start-time-handle {
  stroke: #10b981;
}

.end-time-handle {
  stroke: #ef4444;
}

.time-handle:hover,
.time-handle.dragging {
  transform: scale(1.2);
  cursor: grabbing;
}

.handle-text {
  fill: #ffffff;
  font-size: 8px;
  font-weight: 600;
  text-anchor: middle;
  dominant-baseline: central;
  pointer-events: none;
}

.clock-center {
  fill: #374151;
}

.selected-times {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  align-items: center;
  padding: 1rem;
  background: #f1f5f9;
  border-radius: 12px;
  min-width: 200px;
}

.time-display {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.1rem;
}

.time-label {
  color: #64748b;
  font-weight: 500;
}

.time-value {
  color: #1e293b;
  font-weight: 600;
  font-family: 'Courier New', monospace;
}

.overnight-indicator {
  margin-top: 0.5rem;
  padding: 0.5rem 1rem;
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  color: white;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
}

.overnight-text {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.preset-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  justify-content: center;
  margin-top: 1.5rem;
}

.preset-button {
  padding: 0.5rem 1rem;
  background: #f8fafc;
  border: 2px solid #e2e8f0;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
}

.preset-button:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #475569;
  transform: translateY(-1px);
}

.preset-button.active {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  border-color: #3b82f6;
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

/* レスポンシブ対応 */
@media (max-width: 640px) {
  .clock-time-picker {
    padding: 1rem;
  }
  
  .clock-svg {
    max-width: 300px;
  }
  
  .selected-times {
    flex-direction: row;
    justify-content: space-around;
    width: 100%;
  }
  
  .time-display {
    flex-direction: column;
    text-align: center;
    gap: 0.25rem;
  }
  
  .preset-buttons {
    gap: 0.5rem;
  }
  
  .preset-button {
    padding: 0.4rem 0.8rem;
    font-size: 0.8rem;
  }
}
</style> 