<template>
  <div class="welcome-page">
    <!-- 纸质纹理背景 -->
    <div class="paper-texture"></div>

    <!-- 流动的墨滴背景装饰 -->
    <svg class="ink-flow" viewBox="0 0 1440 900" preserveAspectRatio="xMidYMid slice">
      <defs>
        <linearGradient id="inkGradient" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" style="stop-color:#1a1d23;stop-opacity:0.08" />
          <stop offset="50%" style="stop-color:#7c8b7d;stop-opacity:0.05" />
          <stop offset="100%" style="stop-color:#d4a855;stop-opacity:0.02" />
        </linearGradient>
        <filter id="blurFilter">
          <feGaussianBlur in="SourceGraphic" stdDeviation="40" />
        </filter>
      </defs>
      <!-- 主要墨滴 -->
      <g filter="url(#blurFilter)">
        <path
          class="ink-drop ink-drop-1"
          d="M800,300 C750,250 650,200 580,250 C480,320 420,450 500,550 C550,620 650,580 700,520 C750,460 850,380 800,300 Z"
          fill="url(#inkGradient)"
        />
        <path
          class="ink-drip ink-drip-2"
          d="M950,200 C920,150 850,100 800,120 C720,150 680,250 720,320 C750,370 820,340 850,290 C880,240 980,200 950,200 Z"
          fill="url(#inkGradient)"
        />
        <path
          class="ink-splash ink-splash-3"
          d="M300,400 C280,350 240,300 200,330 C140,380 120,480 160,540 C190,580 240,560 270,520 C300,480 320,450 300,400 Z"
          fill="url(#inkGradient)"
        />
        <!-- 金色点缀 -->
        <circle class="gold-accent gold-accent-1" cx="720" cy="450" r="8" fill="#d4a855" opacity="0.4" />
        <circle class="gold-accent gold-accent-2" cx="850" cy="350" r="6" fill="#d4a855" opacity="0.3" />
        <circle class="gold-accent gold-accent-3" cx="620" cy="380" r="10" fill="#d4a855" opacity="0.35" />
      </g>
    </svg>

    <!-- 主要内容区域 -->
    <div class="welcome-container">
      <!-- 装饰性印章 -->
      <div class="seal-decoration">
        <div class="seal-inner">
          <span class="seal-text">专注</span>
        </div>
      </div>

      <!-- 主标题区域 -->
      <div class="hero-section">
        <h1 class="hero-title">
          <span class="title-char" style="--delay: 0.1s">墨</span>
          <span class="title-char" style="--delay: 0.15s">韵</span>
          <span class="title-char" style="--delay: 0.2s">学</span>
          <span class="title-char" style="--delay: 0.25s">习</span>
        </h1>
        <p class="hero-subtitle">
          <span class="subtitle-line" style="--delay: 0.4s">心如止水</span>
          <span class="subtitle-divider">·</span>
            <span class="subtitle-line" style="--delay: 0.5s">笔耕不辍</span>
          </p>
        <p class="hero-description" style="--delay: 0.6s">
            以专注之心，规划学习之路；以坚持之力，成就自我成长
        </p>
      </div>

      <!-- 快速入口卡片 -->
      <div class="quick-actions">
        <div
          v-for="(action, index) in quickActions"
          :key="action.id"
          class="action-card"
          :class="`action-${index + 1}`"
          :style="{ '--delay': `${0.7 + index * 0.12}s` }"
          @click="handleAction(action.path)"
        >
          <div class="action-icon" :style="{ background: `${action.color}15`, color: action.color }">
            <el-icon :size="28">
              <component :is="action.icon" />
            </el-icon>
          </div>
          <h3 class="action-title">{{ action.title }}</h3>
          <p class="action-desc">{{ action.description }}</p>
          <div class="action-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>
      </div>

      <!-- CTA 按钮 -->
      <div class="cta-section" style="--delay: 1.1s">
        <button class="cta-button" @click="goToDashboard">
          <span class="cta-text">开始今日学习</span>
          <el-icon class="cta-icon"><Right /></el-icon>
        </button>
        <p class="cta-hint">
          <el-icon><Clock /></el-icon>
          <span>{{ greetingMessage }}</span>
        </p>
      </div>
    </div>

    <!-- 底部装饰元素 -->
    <div class="bottom-decoration">
      <div class="decoration-line"></div>
      <p class="decoration-text">
        墨韵学习 · 专注成长 · 每日精进
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Odometer, List, Flag, Document, ArrowRight, Right, Clock } from '@element-plus/icons-vue'

const router = useRouter()

const quickActions = [
  {
    id: 1,
    title: '查看仪表盘',
    description: '今日进度一目了然',
    icon: 'Odometer',
    color: '#7c8b7d',
    path: '/dashboard'
  },
  {
    id: 2,
    title: '管理任务',
    description: '规划今日学习内容',
    icon: 'List',
    color: '#c45050',
    path: '/task'
  },
  {
    id: 3,
    title: '学习方向',
    description: '设置长期目标',
    icon: 'Flag',
    color: '#d4a855',
    path: '/plan'
  },
  {
    id: 4,
    title: '备忘录',
    description: '记录学习灵感',
    icon: 'Document',
    color: '#1a1d23',
    path: '/memo'
  }
]

const greetingMessage = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了，早些休息'
  if (hour < 9) return '清晨时光，一日之计'
  if (hour < 12) return '上午时光，专注学习'
  if (hour < 14) return '午间休息，养精蓄锐'
  if (hour < 18) return '下午时光，继续前行'
  if (hour < 22) return '傍晚时分，总结今日'
  return '晚间时光，规划明日'
})

const handleAction = (path) => {
  router.push(path)
}

const goToDashboard = () => {
  router.push('/dashboard')
}

onMounted(() => {
  // 可以在这里添加初始化逻辑
})
</script>

<style scoped>
/* ═══════════════════════════════════════════════════════════════
   Base Layout
   ═══════════════════════════════════════════════════════════════ */
.welcome-page {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #faf8f3 0%, #f5f2ea 50%, #ebe7dd 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.paper-texture {
  position: absolute;
  inset: 0;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.65' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)' opacity='0.04'/%3E%3C/svg%3E");
  pointer-events: none;
  z-index: 1;
}

/* ═══════════════════════════════════════════════════════════════
   Ink Flow Background
   ═══════════════════════════════════════════════════════════════ */
.ink-flow {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  z-index: 2;
  pointer-events: none;
}

.ink-drop {
  animation: inkFloat 20s ease-in-out infinite;
}

.ink-drip {
  animation: inkDrip 25s ease-in-out infinite;
  animation-delay: -8s;
}

.ink-splash {
  animation: inkSplash 18s ease-in-out infinite;
  animation-delay: -5s;
}

.gold-accent {
  animation: goldGlow 6s ease-in-out infinite;
}

.gold-accent-1 { animation-delay: 0s; }
.gold-accent-2 { animation-delay: -2s; }
.gold-accent-3 { animation-delay: -4s; }

@keyframes inkFloat {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg) scale(1);
  }
  33% {
    transform: translate(30px, -20px) rotate(3deg) scale(1.05);
  }
  66% {
    transform: translate(-20px, 15px) rotate(-2deg) scale(0.98);
  }
}

@keyframes inkDrip {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(-40px, 30px) scale(1.08);
  }
}

@keyframes inkSplash {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  50% {
    transform: translate(25px, -35px) rotate(5deg);
  }
}

@keyframes goldGlow {
  0%, 100% {
    opacity: 0.3;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(1.2);
  }
}

/* ═══════════════════════════════════════════════════════════════
   Welcome Container
   ═══════════════════════════════════════════════════════════════ */
.welcome-container {
  position: relative;
  z-index: 10;
  max-width: 1200px;
  width: 100%;
  padding: 80px 40px;
  text-align: center;
}

/* ═══════════════════════════════════════════════════════════════
   Seal Decoration
   ═══════════════════════════════════════════════════════════════ */
.seal-decoration {
  position: absolute;
  top: -20px;
  right: 60px;
  width: 80px;
  height: 80px;
  animation: sealFloat 12s ease-in-out infinite;
  z-index: -1;
}

.seal-inner {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #c45050 0%, #a84040 100%);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 32px rgba(196, 80, 80, 0.25);
  position: relative;
}

.seal-inner::before {
  content: '';
  position: absolute;
  inset: 4px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 2px;
}

.seal-text {
  font-family: var(--font-display);
  font-size: 28px;
  color: var(--paper-white);
  letter-spacing: 0.15em;
  font-weight: 500;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

@keyframes sealFloat {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-10px) rotate(2deg);
  }
}

/* ═══════════════════════════════════════════════════════════════
   Hero Section
   ═══════════════════════════════════════════════════════════════ */
.hero-section {
  margin-bottom: 72px;
  position: relative;
}

.hero-title {
  font-family: var(--font-display);
  font-size: 96px;
  font-weight: 600;
  color: var(--ink-deep);
  letter-spacing: 0.2em;
  margin: 0 0 24px;
  display: flex;
  justify-content: center;
  gap: 16px;
}

.title-char {
  display: inline-block;
  opacity: 0;
  transform: translateY(30px);
  animation: charReveal 0.8s var(--ease-smooth) forwards;
  animation-delay: var(--delay);
}

.title-char:nth-child(2) {
  background: linear-gradient(135deg, #7c8b7d 0%, #5a665a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.title-char:nth-child(4) {
  background: linear-gradient(135deg, #d4a855 0%, #b89440 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

@keyframes charReveal {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.hero-subtitle {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  font-size: 18px;
  color: var(--sage);
  letter-spacing: 0.15em;
  margin: 0 0 20px;
}

.subtitle-line {
  opacity: 0;
  transform: translateX(-20px);
  animation: slideInLeft 0.6s var(--ease-smooth) forwards;
  animation-delay: var(--delay);
}

@keyframes slideInLeft {
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.subtitle-divider {
  opacity: 0.5;
}

.hero-description {
  font-size: 16px;
  color: var(--ink-light);
  letter-spacing: 0.08em;
  max-width: 560px;
  margin: 0 auto;
  opacity: 0;
  transform: translateY(20px);
  animation: fadeUp 0.6s var(--ease-smooth) forwards;
  animation-delay: var(--delay);
  line-height: 1.8;
}

@keyframes fadeUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ═══════════════════════════════════════════════════════════════
   Quick Actions
   ═══════════════════════════════════════════════════════════════ */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 64px;
}

.action-card {
  position: relative;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(20px);
  border-radius: var(--radius-xl);
  padding: 32px 24px;
  text-align: center;
  cursor: pointer;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: var(--shadow-md);
  transition: all 0.4s var(--ease-smooth);
  opacity: 0;
  transform: translateY(30px);
  animation: cardReveal 0.7s var(--ease-smooth) forwards;
  animation-delay: var(--delay);
  overflow: hidden;
}

@keyframes cardReveal {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.action-card::before {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at center, rgba(255, 255, 255, 0.8), transparent 70%);
  opacity: 0;
  transition: opacity 0.4s var(--ease-smooth);
}

.action-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-float);
  border-color: rgba(255, 255, 255, 1);
}

.action-card:hover::before {
  opacity: 1;
}

.action-icon {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  transition: all 0.4s var(--ease-smooth);
  position: relative;
  z-index: 1;
}

.action-card:hover .action-icon {
  transform: scale(1.1) rotate(5deg);
}

.action-title {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 600;
  color: var(--ink-deep);
  letter-spacing: 0.08em;
  margin: 0 0 8px;
  position: relative;
  z-index: 1;
}

.action-desc {
  font-size: 13px;
  color: var(--ink-light);
  letter-spacing: 0.05em;
  margin: 0;
  position: relative;
  z-index: 1;
}

.action-arrow {
  position: absolute;
  bottom: 16px;
  right: 16px;
  opacity: 0;
  transform: translateX(-10px);
  transition: all 0.3s var(--ease-fast);
  color: var(--ink-deep);
}

.action-card:hover .action-arrow {
  opacity: 1;
  transform: translateX(0);
}

/* ═══════════════════════════════════════════════════════════════
   CTA Section
   ═══════════════════════════════════════════════════════════════ */
.cta-section {
  opacity: 0;
  transform: translateY(20px);
  animation: fadeUp 0.6s var(--ease-smooth) forwards;
  animation-delay: var(--delay);
}

.cta-button {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 12px;
  background: var(--gradient-ink);
  color: var(--paper-white);
  border: none;
  border-radius: 60px;
  padding: 18px 48px;
  font-size: 16px;
  letter-spacing: 0.08em;
  cursor: pointer;
  box-shadow: 0 12px 40px rgba(26, 29, 35, 0.3);
  transition: all 0.4s var(--ease-smooth);
  overflow: hidden;
}

.cta-button::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, transparent, rgba(255, 255, 255, 0.1));
  opacity: 0;
  transition: opacity 0.4s var(--ease-smooth);
}

.cta-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 16px 48px rgba(26, 29, 35, 0.4);
}

.cta-button:hover::before {
  opacity: 1;
}

.cta-button:hover .cta-icon {
  transform: translateX(4px);
}

.cta-icon {
  transition: transform 0.3s var(--ease-fast);
}

.cta-text {
  font-weight: 500;
}

.cta-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 16px;
  font-size: 13px;
  color: var(--muted);
  letter-spacing: 0.05em;
}

.cta-hint .el-icon {
  font-size: 14px;
  color: var(--sage);
}

/* ═══════════════════════════════════════════════════════════════
   Bottom Decoration
   ═══════════════════════════════════════════════════════════════ */
.bottom-decoration {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  z-index: 10;
}

.decoration-line {
  width: 120px;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--gold-subtle), transparent);
  margin: 0 auto 16px;
  opacity: 0.6;
}

.decoration-text {
  font-size: 12px;
  color: var(--muted);
  letter-spacing: 0.15em;
  margin: 0;
}

/* ═══════════════════════════════════════════════════════════════
   Responsive
   ═══════════════════════════════════════════════════════════════ */
@media (max-width: 1024px) {
  .quick-actions {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }

  .hero-title {
    font-size: 72px;
  }
}

@media (max-width: 640px) {
  .welcome-container {
    padding: 60px 24px;
  }

  .hero-title {
    font-size: 56px;
    gap: 8px;
  }

  .quick-actions {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .seal-decoration {
    display: none;
  }
}
</style>
