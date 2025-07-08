<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { RouterLink, RouterView } from 'vue-router'

const isMobileMenuOpen = ref(false)
const isMobile = ref(false)

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value
}

const closeMobileMenu = () => {
  isMobileMenuOpen.value = false
}

const checkScreenSize = () => {
  isMobile.value = window.innerWidth < 768
  if (!isMobile.value) {
    isMobileMenuOpen.value = false
  }
}

onMounted(() => {
  checkScreenSize()
  window.addEventListener('resize', checkScreenSize)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkScreenSize)
})
</script>

<template>
  <div id="app">
    <header class="app-header">
      <div class="header-content">
        <div class="header-left">
          <h1 class="app-title">
            <span class="title-icon">⏰</span>
            <span class="title-text">
              <span class="title-main">TimeRangeChecker</span>
              <span class="title-sub" v-if="!isMobile">現在時刻が指定範囲内にあるかチェック</span>
            </span>
          </h1>
        </div>
        
        <!-- デスクトップナビゲーション -->
        <nav class="nav-menu desktop-nav" v-if="!isMobile">
          <RouterLink to="/" class="nav-link" active-class="nav-link-active" @click="closeMobileMenu">
            <span class="nav-icon">🏠</span>
            ホーム
          </RouterLink>
          <RouterLink to="/about" class="nav-link" active-class="nav-link-active" @click="closeMobileMenu">
            <span class="nav-icon">ℹ️</span>
            About
          </RouterLink>
        </nav>

        <!-- モバイルメニューボタン -->
        <button 
          v-if="isMobile" 
          @click="toggleMobileMenu" 
          class="mobile-menu-button"
          :class="{ active: isMobileMenuOpen }"
          aria-label="メニューを開く"
        >
          <span class="hamburger-line"></span>
          <span class="hamburger-line"></span>
          <span class="hamburger-line"></span>
        </button>
      </div>

      <!-- モバイルナビゲーション -->
      <nav 
        v-if="isMobile" 
        class="mobile-nav" 
        :class="{ open: isMobileMenuOpen }"
      >
        <div class="mobile-nav-content">
          <RouterLink to="/" class="mobile-nav-link" active-class="mobile-nav-link-active" @click="closeMobileMenu">
            <span class="nav-icon">🏠</span>
            ホーム
          </RouterLink>
          <RouterLink to="/about" class="mobile-nav-link" active-class="mobile-nav-link-active" @click="closeMobileMenu">
            <span class="nav-icon">ℹ️</span>
            About
          </RouterLink>
        </div>
      </nav>
    </header>

    <!-- メインコンテンツ -->
    <main class="app-main">
      <RouterView />
    </main>

    <!-- フッター -->
    <footer class="app-footer">
      <div class="footer-content">
        <p>&copy; 2025 TimeRangeChecker</p>
        <div class="footer-links" v-if="!isMobile">
          <span class="footer-link">高性能な時間範囲チェック</span>
          <span class="footer-divider">|</span>
          <span class="footer-link">リアルタイム統計</span>
        </div>
      </div>
    </footer>

    <!-- モバイルメニューオーバーレイ -->
    <div 
      v-if="isMobile && isMobileMenuOpen" 
      class="mobile-overlay" 
      @click="closeMobileMenu"
    ></div>
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
  overflow-x: hidden;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* スムーズスクロール */
html {
  scroll-behavior: smooth;
}

/* カスタムスクロールバー */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}
</style>

<style scoped>
.app-header {
  background: linear-gradient(135deg, #1e3a8a, #3730a3);
  color: white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(10px);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
}

.header-left {
  flex: 1;
}

.app-title {
  font-size: 1.8rem;
  font-weight: 700;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.title-icon {
  font-size: 2rem;
  animation: clockTick 2s ease-in-out infinite;
}

@keyframes clockTick {
  0%, 50%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(10deg); }
  75% { transform: rotate(-10deg); }
}

.title-text {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}

.title-main {
  font-size: 1.8rem;
  font-weight: 700;
}

.title-sub {
  font-size: 0.9rem;
  font-weight: 400;
  opacity: 0.9;
  color: #e2e8f0;
}

/* デスクトップナビゲーション */
.desktop-nav {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #e2e8f0;
  text-decoration: none;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  position: relative;
  overflow: hidden;
}

.nav-link::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  transition: left 0.5s ease;
}

.nav-link:hover::before {
  left: 100%;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  transform: translateY(-2px);
}

.nav-link-active {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.nav-icon {
  font-size: 1.1rem;
}

/* モバイルメニューボタン */
.mobile-menu-button {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 40px;
  height: 40px;
  background: none;
  border: none;
  cursor: pointer;
  gap: 4px;
  transition: transform 0.3s ease;
}

.mobile-menu-button:hover {
  transform: scale(1.1);
}

.hamburger-line {
  width: 24px;
  height: 3px;
  background: white;
  border-radius: 2px;
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  transform-origin: center;
}

.mobile-menu-button.active .hamburger-line:nth-child(1) {
  transform: translateY(7px) rotate(45deg);
}

.mobile-menu-button.active .hamburger-line:nth-child(2) {
  opacity: 0;
  transform: scaleX(0);
}

.mobile-menu-button.active .hamburger-line:nth-child(3) {
  transform: translateY(-7px) rotate(-45deg);
}

/* モバイルナビゲーション */
.mobile-nav {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: linear-gradient(135deg, #1e3a8a, #3730a3);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  transform: translateY(-100%);
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  z-index: 99;
}

.mobile-nav.open {
  transform: translateY(0);
  opacity: 1;
  visibility: visible;
}

.mobile-nav-content {
  padding: 1rem 2rem 2rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.mobile-nav-link {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: #e2e8f0;
  text-decoration: none;
  padding: 1rem;
  border-radius: 12px;
  font-weight: 500;
  font-size: 1.1rem;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.mobile-nav-link:hover,
.mobile-nav-link-active {
  background: rgba(255, 255, 255, 0.15);
  color: white;
  transform: translateX(8px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.mobile-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 98;
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* メインコンテンツ */
.app-main {
  flex: 1;
  min-height: calc(100vh - 140px);
}

/* フッター */
.app-footer {
  background: linear-gradient(135deg, #374151, #1f2937);
  color: #e5e7eb;
  padding: 1.5rem 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  text-align: center;
}

.footer-links {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 0.9rem;
  opacity: 0.8;
}

.footer-link {
  transition: color 0.2s ease;
}

.footer-link:hover {
  color: #60a5fa;
}

.footer-divider {
  opacity: 0.5;
}

/* タブレット対応 */
@media (max-width: 1024px) {
  .header-content {
    padding: 1rem 1.5rem;
  }
  
  .app-title {
    font-size: 1.6rem;
  }
  
  .title-main {
    font-size: 1.6rem;
  }
  
  .title-sub {
    font-size: 0.8rem;
  }
}

/* モバイル対応 */
@media (max-width: 768px) {
  .header-content {
    padding: 1rem;
  }
  
  .app-title {
    font-size: 1.4rem;
  }
  
  .title-main {
    font-size: 1.4rem;
  }
  
  .title-icon {
    font-size: 1.6rem;
  }
  
  .footer-content {
    padding: 0 1rem;
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .app-main {
    min-height: calc(100vh - 160px);
  }
}

/* 小さなモバイル画面 */
@media (max-width: 480px) {
  .header-content {
    padding: 0.75rem;
  }
  
  .app-title {
    font-size: 1.2rem;
    gap: 0.5rem;
  }
  
  .title-main {
    font-size: 1.2rem;
  }
  
  .title-icon {
    font-size: 1.4rem;
  }
  
  .mobile-nav-content {
    padding: 1rem;
  }
  
  .mobile-nav-link {
    padding: 0.75rem;
    font-size: 1rem;
  }
}

/* 横向きモバイル */
@media (max-height: 500px) and (orientation: landscape) {
  .app-header {
    position: sticky;
  }
  
  .mobile-nav {
    max-height: 200px;
    overflow-y: auto;
  }
}

/* ダークモード対応 */
@media (prefers-color-scheme: dark) {
  body {
    background-color: #0f172a;
    color: #e2e8f0;
  }
}

/* プリントスタイル */
@media print {
  .app-header,
  .app-footer,
  .mobile-nav,
  .mobile-overlay {
    display: none;
  }
  
  .app-main {
    min-height: auto;
  }
}

/* 高コントラストモード */
@media (prefers-contrast: high) {
  .nav-link,
  .mobile-nav-link {
    border: 2px solid rgba(255, 255, 255, 0.5);
  }
  
  .nav-link:hover,
  .mobile-nav-link:hover {
    border-color: white;
  }
}

/* モーション軽減 */
@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
  
  .title-icon {
    animation: none;
  }
}
</style>
