<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div id="app">
    <header class="app-header">
      <div class="header-content">
        <div class="header-left">
          <h1 class="app-title">
            <span class="title-icon">⏰</span>
            TimeRangeChecker
          </h1>
          <p class="app-subtitle">現在時刻が指定範囲内にあるかチェック</p>
        </div>
        
        <nav class="nav-menu">
          <RouterLink to="/" class="nav-link" active-class="nav-link-active">
            ホーム
          </RouterLink>
          <RouterLink to="/about" class="nav-link" active-class="nav-link-active">
            About
          </RouterLink>
        </nav>
      </div>
    </header>

    <main class="app-main">
      <RouterView />
    </main>

    <footer class="app-footer">
      <div class="footer-content">
        <p>&copy; 2024 TimeRangeChecker. 高性能な時間範囲チェックアプリケーション</p>
      </div>
    </footer>
  </div>
</template>

<style>
/* グローバルスタイル */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Arial, 'Hiragino Kaku Gothic ProN', 'Hiragino Sans', Meiryo, sans-serif;
  line-height: 1.6;
  color: #2c3e50;
  background-color: #f8fafc;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
</style>

<style scoped>
.app-header {
  background: linear-gradient(135deg, #1e3a8a, #3730a3);
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 10;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1.5rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  flex: 1;
}

.app-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 0.25rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.title-icon {
  font-size: 2.2rem;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
}

.app-subtitle {
  font-size: 0.95rem;
  opacity: 0.9;
  font-weight: 400;
  margin: 0;
}

.nav-menu {
  display: flex;
  gap: 2rem;
  align-items: center;
}

.nav-link {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  font-weight: 500;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  transition: all 0.2s ease;
  position: relative;
}

.nav-link:hover {
  color: white;
  background-color: rgba(255, 255, 255, 0.1);
}

.nav-link-active {
  color: white;
  background-color: rgba(255, 255, 255, 0.2);
  font-weight: 600;
}

.app-main {
  flex: 1;
}

.app-footer {
  background: #1f2937;
  color: #9ca3af;
  padding: 1.5rem 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
  padding: 0 2rem;
}

.footer-content p {
  font-size: 0.9rem;
}

/* レスポンシブデザイン */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
  }
  
  .app-title {
    font-size: 1.6rem;
  }
  
  .nav-menu {
    gap: 1rem;
    justify-content: center;
  }
  
  .nav-link {
    padding: 0.4rem 0.8rem;
    font-size: 0.9rem;
  }
}

@media (max-width: 480px) {
  .app-title {
    font-size: 1.4rem;
  }
  
  .title-icon {
    font-size: 1.6rem;
  }
  
  .app-subtitle {
    font-size: 0.85rem;
  }
}
</style>
