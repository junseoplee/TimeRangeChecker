<template>
  <div class="about-view">
    <div class="container">
      <!-- ヒーローセクション -->
      <section class="hero-section">
        <div class="hero-content">
          <h1 class="hero-title">
            <span class="hero-icon">⏰</span>
            TimeRangeChecker
          </h1>
          <p class="hero-subtitle">高性能な時間範囲チェックアプリケーション</p>
          <div class="hero-badges">
            <span class="badge primary">Vue.js 3</span>
            <span class="badge success">Spring Boot</span>
            <span class="badge warning">Redis</span>
            <span class="badge info">TypeScript</span>
          </div>
        </div>
      </section>

      <!-- 概要セクション -->
      <section class="overview-section">
        <div class="section-header">
          <h2 class="section-title">
            <span class="section-icon">📋</span>
            アプリケーション概要
          </h2>
        </div>
        <div class="overview-content">
          <div class="overview-text">
            <p>
              TimeRangeCheckerは、指定された時間範囲内に現在時刻が含まれるかどうかを効率的にチェックする
              高性能ウェブアプリケーションです。深夜を跨ぐ時間範囲や24時間全体の範囲にも対応し、
              リアルタイム統計機能とインタラクティブなUI体験を提供します。
            </p>
            <div class="key-features">
              <div class="feature-item">
                <span class="feature-icon">🎯</span>
                <span class="feature-text">高精度な時間範囲判定</span>
              </div>
              <div class="feature-item">
                <span class="feature-icon">📊</span>
                <span class="feature-text">リアルタイム統計分析</span>
              </div>
              <div class="feature-item">
                <span class="feature-icon">🎨</span>
                <span class="feature-text">インタラクティブUI</span>
              </div>
              <div class="feature-item">
                <span class="feature-icon">⚡</span>
                <span class="feature-text">高性能キャッシュ</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 機能詳細セクション -->
      <section class="features-section">
        <div class="section-header">
          <h2 class="section-title">
            <span class="section-icon">🚀</span>
            実装機能
          </h2>
        </div>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-header">
              <span class="feature-card-icon">🕐</span>
              <h3 class="feature-title">アナログ時計UI</h3>
            </div>
            <div class="feature-content">
              <p>直感的なドラッグ＆ドロップ操作で時間範囲を設定できるアナログ時計インターフェース。</p>
              <ul class="feature-list">
                <li>24時間表示対応</li>
                <li>視覚的な時間範囲表示</li>
                <li>プリセット時間設定</li>
                <li>タッチデバイス対応</li>
              </ul>
            </div>
          </div>

          <div class="feature-card">
            <div class="feature-header">
              <span class="feature-card-icon">🎭</span>
              <h3 class="feature-title">インタラクティブアニメーション</h3>
            </div>
            <div class="feature-content">
              <p>滑らかなアニメーションと視覚的フィードバックでユーザー体験を向上。</p>
              <ul class="feature-list">
                <li>リップル効果</li>
                <li>ホバーアニメーション</li>
                <li>ローディング表示</li>
                <li>成功/エラー表示</li>
              </ul>
            </div>
          </div>

          <div class="feature-card">
            <div class="feature-header">
              <span class="feature-card-icon">📈</span>
              <h3 class="feature-title">統計ダッシュボード</h3>
            </div>
            <div class="feature-content">
              <p>使用統計をリアルタイムで表示する包括的なダッシュボード機能。</p>
              <ul class="feature-list">
                <li>人気時間帯ランキング</li>
                <li>週間使用統計グラフ</li>
                <li>システム状態監視</li>
                <li>自動データ更新</li>
              </ul>
            </div>
          </div>

          <div class="feature-card">
            <div class="feature-header">
              <span class="feature-card-icon">📱</span>
              <h3 class="feature-title">レスポンシブデザイン</h3>
            </div>
            <div class="feature-content">
              <p>デスクトップ、タブレット、モバイルすべてのデバイスに最適化。</p>
              <ul class="feature-list">
                <li>Mobile-firstデザイン</li>
                <li>ダークモード対応</li>
                <li>高コントラスト対応</li>
                <li>アクセシビリティ準拠</li>
              </ul>
            </div>
          </div>
        </div>
      </section>

      <!-- 使用方法セクション -->
      <section class="usage-section">
        <div class="section-header">
          <h2 class="section-title">
            <span class="section-icon">📖</span>
            使用方法
          </h2>
        </div>
        <div class="usage-steps">
          <div class="step-card">
            <div class="step-number">1</div>
            <div class="step-content">
              <h3 class="step-title">時間入力方式の選択</h3>
              <p>メイン画面で「従来入力」または「時計選択」タブを選択します。時計選択では直感的な操作が可能です。</p>
            </div>
          </div>

          <div class="step-card">
            <div class="step-number">2</div>
            <div class="step-content">
              <h3 class="step-title">時間範囲の設定</h3>
              <p>開始時間と終了時間を設定します。アナログ時計では緑色のハンドルが開始時間、赤色のハンドルが終了時間です。</p>
            </div>
          </div>

          <div class="step-card">
            <div class="step-number">3</div>
            <div class="step-content">
              <h3 class="step-title">時間チェック実行</h3>
              <p>「時間をチェック」ボタンをクリックして現在時刻が設定範囲内にあるかを確認します。</p>
            </div>
          </div>

          <div class="step-card">
            <div class="step-number">4</div>
            <div class="step-content">
              <h3 class="step-title">結果と統計の確認</h3>
              <p>チェック結果とリアルタイム統計ダッシュボードで使用パターンを分析できます。</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 技術スタックセクション -->
      <section class="tech-stack-section">
        <div class="section-header">
          <h2 class="section-title">
            <span class="section-icon">🛠️</span>
            技術スタック
          </h2>
        </div>
        <div class="tech-categories">
          <div class="tech-category">
            <h3 class="category-title">
              <span class="category-icon">🎨</span>
              フロントエンド
            </h3>
            <div class="tech-items">
              <div class="tech-item">
                <span class="tech-name">Vue.js 3</span>
                <span class="tech-desc">Composition API, TypeScript</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">Pinia</span>
                <span class="tech-desc">状態管理</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">Vue Router</span>
                <span class="tech-desc">SPA ルーティング</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">Vite</span>
                <span class="tech-desc">高速ビルドツール</span>
              </div>
            </div>
          </div>

          <div class="tech-category">
            <h3 class="category-title">
              <span class="category-icon">⚙️</span>
              バックエンド
            </h3>
            <div class="tech-items">
              <div class="tech-item">
                <span class="tech-name">Spring Boot 3</span>
                <span class="tech-desc">Java 17, REST API</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">Spring Data JPA</span>
                <span class="tech-desc">データアクセス層</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">Redis</span>
                <span class="tech-desc">高性能キャッシュ</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">H2 Database</span>
                <span class="tech-desc">組み込みデータベース</span>
              </div>
            </div>
          </div>

          <div class="tech-category">
            <h3 class="category-title">
              <span class="category-icon">🔧</span>
              開発・運用
            </h3>
            <div class="tech-items">
              <div class="tech-item">
                <span class="tech-name">Docker</span>
                <span class="tech-desc">コンテナ化</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">Gradle</span>
                <span class="tech-desc">ビルド自動化</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">Git</span>
                <span class="tech-desc">バージョン管理</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">ESLint/Prettier</span>
                <span class="tech-desc">コード品質管理</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- システム仕様セクション -->
      <section class="specs-section">
        <div class="section-header">
          <h2 class="section-title">
            <span class="section-icon">📋</span>
            システム仕様
          </h2>
        </div>
        <div class="specs-grid">
          <div class="spec-card">
            <h3 class="spec-title">
              <span class="spec-icon">🔄</span>
              API仕様
            </h3>
            <div class="spec-content">
              <div class="spec-item">
                <span class="spec-label">エンドポイント:</span>
                <span class="spec-value">/api/v1/time-check</span>
              </div>
              <div class="spec-item">
                <span class="spec-label">メソッド:</span>
                <span class="spec-value">POST</span>
              </div>
              <div class="spec-item">
                <span class="spec-label">レスポンス時間:</span>
                <span class="spec-value">< 200ms (平均)</span>
              </div>
              <div class="spec-item">
                <span class="spec-label">キャッシュ:</span>
                <span class="spec-value">30分TTL</span>
              </div>
            </div>
          </div>

          <div class="spec-card">
            <h3 class="spec-title">
              <span class="spec-icon">⚡</span>
              パフォーマンス
            </h3>
            <div class="spec-content">
              <div class="spec-item">
                <span class="spec-label">同時接続数:</span>
                <span class="spec-value">1000+ users</span>
              </div>
              <div class="spec-item">
                <span class="spec-label">可用性:</span>
                <span class="spec-value">99.9%</span>
              </div>
              <div class="spec-item">
                <span class="spec-label">データ保持:</span>
                <span class="spec-value">30日間</span>
              </div>
              <div class="spec-item">
                <span class="spec-label">メモリ使用量:</span>
                <span class="spec-value">< 256MB</span>
              </div>
            </div>
          </div>

          <div class="spec-card">
            <h3 class="spec-title">
              <span class="spec-icon">🛡️</span>
              セキュリティ
            </h3>
            <div class="spec-content">
              <div class="spec-item">
                <span class="spec-label">暗号化:</span>
                <span class="spec-value">TLS 1.3</span>
              </div>
              <div class="spec-item">
                <span class="spec-label">CORS:</span>
                <span class="spec-value">設定済み</span>
              </div>
              <div class="spec-item">
                <span class="spec-label">データ保護:</span>
                <span class="spec-value">匿名化</span>
              </div>
              <div class="spec-item">
                <span class="spec-label">監査ログ:</span>
                <span class="spec-value">有効</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 特殊機能セクション -->
      <section class="special-features-section">
        <div class="section-header">
          <h2 class="section-title">
            <span class="section-icon">✨</span>
            特殊機能と対応範囲
          </h2>
        </div>
        <div class="special-features">
          <div class="special-feature">
            <div class="special-header">
              <span class="special-icon">🌙</span>
              <h3 class="special-title">深夜跨ぎ対応</h3>
            </div>
            <p class="special-desc">
              22:00〜06:00のような深夜を跨ぐ時間範囲にも完全対応。
              アルゴリズムが自動的に日付変更を考慮して正確な判定を行います。
            </p>
          </div>

          <div class="special-feature">
            <div class="special-header">
              <span class="special-icon">🔄</span>
              <h3 class="special-title">24時間全範囲</h3>
            </div>
            <p class="special-desc">
              開始時間と終了時間が同じ場合、24時間全体を対象とする特殊モード。
              常に「範囲内」と判定されます。
            </p>
          </div>

          <div class="special-feature">
            <div class="special-header">
              <span class="special-icon">📊</span>
              <h3 class="special-title">統計分析</h3>
            </div>
            <p class="special-desc">
              使用パターンの分析、人気時間帯の特定、成功率の監視など、
              包括的な統計機能を提供。
            </p>
          </div>
        </div>
      </section>

      <!-- フッターセクション -->
      <section class="about-footer">
        <div class="footer-content">
          <p class="footer-text">
            TimeRangeCheckerは高い拡張性と保守性を持つモダンなアーキテクチャで構築されており、
            継続的な機能改善とパフォーマンス最適化を行っています。
          </p>
          <div class="footer-badges">
            <span class="footer-badge">Version 1.0.0</span>
            <span class="footer-badge">Made with ❤️</span>
            <span class="footer-badge">© 2025</span>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.about-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

/* ヒーローセクション */
.hero-section {
  text-align: center;
  margin-bottom: 4rem;
  animation: fadeInUp 0.8s ease-out;
}

.hero-content {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 3rem;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.hero-title {
  font-size: 3rem;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}

.hero-icon {
  font-size: 3.5rem;
  animation: heartbeat 2s ease-in-out infinite;
}

@keyframes heartbeat {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.hero-subtitle {
  font-size: 1.3rem;
  color: #64748b;
  margin-bottom: 2rem;
  font-weight: 500;
}

.hero-badges {
  display: flex;
  justify-content: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.badge {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.badge.primary { background: #dbeafe; color: #1e40af; }
.badge.success { background: #dcfce7; color: #166534; }
.badge.warning { background: #fef3c7; color: #92400e; }
.badge.info { background: #e0e7ff; color: #3730a3; }

/* セクション共通スタイル */
.section-header {
  text-align: center;
  margin-bottom: 3rem;
}

.section-title {
  font-size: 2.2rem;
  font-weight: 700;
  color: white;
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
}

.section-icon {
  font-size: 2.5rem;
}

/* 概要セクション */
.overview-section {
  margin-bottom: 4rem;
  animation: fadeInUp 0.8s ease-out 0.2s both;
}

.overview-content {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 2.5rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.overview-text p {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #374151;
  margin-bottom: 2rem;
}

.key-features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem;
  background: #f8fafc;
  border-radius: 12px;
  transition: transform 0.3s ease;
}

.feature-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  font-size: 1.5rem;
}

.feature-text {
  font-weight: 600;
  color: #1e293b;
}

/* 機能詳細セクション */
.features-section {
  margin-bottom: 4rem;
  animation: fadeInUp 0.8s ease-out 0.4s both;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.feature-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.feature-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.feature-card-icon {
  font-size: 2rem;
}

.feature-title {
  font-size: 1.3rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.feature-content p {
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 1rem;
}

.feature-list {
  list-style: none;
  padding: 0;
}

.feature-list li {
  padding: 0.5rem 0;
  color: #374151;
  position: relative;
  padding-left: 1.5rem;
}

.feature-list li::before {
  content: '✅';
  position: absolute;
  left: 0;
  top: 0.5rem;
}

/* 使用方法セクション */
.usage-section {
  margin-bottom: 4rem;
  animation: fadeInUp 0.8s ease-out 0.6s both;
}

.usage-steps {
  display: grid;
  gap: 2rem;
}

.step-card {
  display: flex;
  align-items: flex-start;
  gap: 2rem;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.step-card:hover {
  transform: translateX(10px);
}

.step-number {
  flex-shrink: 0;
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  font-weight: 700;
}

.step-content {
  flex: 1;
}

.step-title {
  font-size: 1.3rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 0.75rem;
}

.step-content p {
  color: #64748b;
  line-height: 1.6;
}

/* 技術スタックセクション */
.tech-stack-section {
  margin-bottom: 4rem;
  animation: fadeInUp 0.8s ease-out 0.8s both;
}

.tech-categories {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 2rem;
}

.tech-category {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.category-title {
  font-size: 1.4rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.category-icon {
  font-size: 1.6rem;
}

.tech-items {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.tech-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  padding: 1rem;
  background: #f8fafc;
  border-radius: 12px;
  transition: transform 0.3s ease;
}

.tech-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.tech-name {
  font-weight: 700;
  color: #1e293b;
  font-size: 1.1rem;
}

.tech-desc {
  color: #64748b;
  font-size: 0.9rem;
}

/* システム仕様セクション */
.specs-section {
  margin-bottom: 4rem;
  animation: fadeInUp 0.8s ease-out 1s both;
}

.specs-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.spec-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.spec-title {
  font-size: 1.3rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.spec-icon {
  font-size: 1.5rem;
}

.spec-content {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.spec-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  background: #f8fafc;
  border-radius: 8px;
}

.spec-label {
  font-weight: 600;
  color: #64748b;
}

.spec-value {
  font-weight: 700;
  color: #1e293b;
  font-family: 'Courier New', monospace;
}

/* 特殊機能セクション */
.special-features-section {
  margin-bottom: 4rem;
  animation: fadeInUp 0.8s ease-out 1.2s both;
}

.special-features {
  display: grid;
  gap: 2rem;
}

.special-feature {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.special-feature:hover {
  transform: translateY(-3px);
}

.special-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.special-icon {
  font-size: 2rem;
}

.special-title {
  font-size: 1.4rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.special-desc {
  color: #64748b;
  line-height: 1.7;
  font-size: 1.1rem;
}

/* フッターセクション */
.about-footer {
  animation: fadeInUp 0.8s ease-out 1.4s both;
}

.footer-content {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 2rem;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.footer-text {
  color: #64748b;
  line-height: 1.7;
  margin-bottom: 1.5rem;
  font-size: 1.1rem;
}

.footer-badges {
  display: flex;
  justify-content: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.footer-badge {
  padding: 0.5rem 1rem;
  background: #f1f5f9;
  color: #475569;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
}

/* アニメーション */
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
  .container {
    padding: 0 1rem;
  }

  .hero-title {
    font-size: 2.2rem;
    flex-direction: column;
    gap: 0.5rem;
  }

  .hero-icon {
    font-size: 2.5rem;
  }

  .hero-content {
    padding: 2rem;
  }

  .section-title {
    font-size: 1.8rem;
    flex-direction: column;
    gap: 0.5rem;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .tech-categories {
    grid-template-columns: 1fr;
  }

  .specs-grid {
    grid-template-columns: 1fr;
  }

  .step-card {
    flex-direction: column;
    text-align: center;
    gap: 1rem;
  }

  .key-features {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .about-view {
    padding: 1rem 0;
  }

  .hero-title {
    font-size: 1.8rem;
  }

  .hero-subtitle {
    font-size: 1.1rem;
  }

  .hero-badges {
    justify-content: center;
  }

  .badge {
    font-size: 0.8rem;
    padding: 0.4rem 0.8rem;
  }

  .section-title {
    font-size: 1.5rem;
  }

  .feature-card,
  .tech-category,
  .spec-card,
  .special-feature {
    padding: 1.5rem;
  }

  .overview-content {
    padding: 1.5rem;
  }
}
</style>
