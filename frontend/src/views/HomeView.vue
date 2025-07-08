<script setup lang="ts">
import TimeInputForm from '@/components/TimeInputForm.vue'
import TimeCheckResult from '@/components/TimeCheckResult.vue'
</script>

<template>
  <div class="home-view">
    <main class="main-content">
      <!-- 메인 시간 체크 섹션 -->
      <section class="time-check-section">
        <TimeInputForm />
        <TimeCheckResult />
      </section>
    </main>
  </div>
</template>

<style scoped>
.home-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem 1rem;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
}

.time-check-section {
  display: flex;
  flex-direction: column;
  gap: 0;
}

/* レスポンシブデザイン */
@media (max-width: 768px) {
  .home-view {
    padding: 1rem 0.5rem;
  }
}
</style>
