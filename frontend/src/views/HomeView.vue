<script setup lang="ts">
import TimeInputForm from '@/components/TimeInputForm.vue'
import TimeCheckResult from '@/components/TimeCheckResult.vue'
import StatsDashboard from '@/components/StatsDashboard.vue'
</script>

<template>
  <div class="home-view">
    <main class="main-content">
      <!-- メイン時間チェックセクション -->
      <section class="time-check-section">
        <TimeInputForm />
        <TimeCheckResult />
      </section>

      <!-- 統計ダッシュボードセクション -->
      <section class="stats-section">
        <StatsDashboard />
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
  display: flex;
  flex-direction: column;
  gap: 3rem;
}

.time-check-section {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.stats-section {
  animation: fadeInUp 0.6s ease-out 0.3s both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* レスポンシブデザイン */
@media (max-width: 768px) {
  .home-view {
    padding: 1rem 0.5rem;
  }
  
  .main-content {
    gap: 2rem;
  }
}
</style>
