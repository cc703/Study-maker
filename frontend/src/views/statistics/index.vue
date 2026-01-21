<template>
  <div class="statistics-page">
    <!-- 总体统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card stat-blue" style="animation-delay: 0.02s;">
        <div class="stat-icon">
          <el-icon><Flag /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboard?.totalPlans || 0 }}</div>
          <div class="stat-label">学习方向</div>
        </div>
      </div>
      <div class="stat-card stat-green" style="animation-delay: 0.08s;">
        <div class="stat-icon">
          <el-icon><List /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboard?.totalTasks || 0 }}</div>
          <div class="stat-label">总任务数</div>
        </div>
      </div>
      <div class="stat-card stat-sage" style="animation-delay: 0.14s;">
        <div class="stat-icon">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboard?.totalCompletedTasks || 0 }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </div>
      <div class="stat-card stat-orange" style="animation-delay: 0.20s;">
        <div class="stat-icon">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboard?.consecutiveDays || 0 }}</div>
          <div class="stat-label">连续学习天数</div>
        </div>
      </div>
    </div>

    <div class="content-row">
      <!-- 学习方向完成情况 -->
      <div class="panel-panel">
        <div class="panel-title">
          <h3>学习方向完成情况</h3>
        </div>
        <div v-if="!dashboard?.planStats?.length" class="empty-state">
          <div class="empty-illustration">
            <el-icon><PieChart /></el-icon>
          </div>
          <p>暂无统计数据</p>
        </div>
        <div v-else class="plan-stats-list">
          <div
            v-for="(stat, index) in dashboard.planStats"
            :key="stat.planId"
            class="plan-stat-item"
            :style="{ animationDelay: `${index * 0.08}s` }"
          >
            <div class="plan-stat-header">
              <div class="plan-info">
                <div class="color-dot" :style="{ backgroundColor: stat.color }"></div>
                <span class="plan-name">{{ stat.planName }}</span>
              </div>
              <div class="plan-rate">{{ stat.completionRate || 0 }}%</div>
            </div>

            <div class="plan-stat-progress">
              <div class="progress-track">
                <div
                  class="progress-bar"
                  :style="{ width: `${stat.completionRate || 0}%`, background: stat.color }"
                ></div>
              </div>
            </div>

            <div class="plan-stat-footer">
              <span>{{ stat.completedTasks || 0 }} / {{ stat.totalTasks || 0 }} 任务</span>
              <span class="time-info">累计 {{ formatMinutes(stat.totalTime) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 本周学习趋势 -->
      <div class="panel-panel">
        <div class="panel-title">
          <h3>本周学习趋势</h3>
        </div>
        <div v-if="!dashboard?.weeklyTrend?.length" class="empty-state">
          <div class="empty-illustration">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <p>暂无本周数据</p>
        </div>
        <div v-else class="weekly-chart">
          <div
            v-for="(day, index) in weekDays"
            :key="index"
            class="day-bar"
          >
            <div class="day-label">{{ formatDayLabel(day) }}</div>
            <div class="day-stats">{{ getWeeklyDayStat(day)?.completedTasks || 0 }}/{{ getWeeklyDayStat(day)?.totalTasks || 0 }}</div>
            <div class="bar-container">
              <div class="bar-track">
                <div
                  class="bar-fill total"
                  :style="{ width: getBarWidth(getWeeklyDayStat(day)?.totalTasks || 0) }"
                ></div>
              </div>
            </div>
            <div class="bar-container">
              <div class="bar-track">
                <div
                  class="bar-fill completed"
                  :style="{ width: getBarWidth(getWeeklyDayStat(day)?.completedTasks || 0) }"
                ></div>
              </div>
            </div>
          </div>
          <div class="chart-legend">
            <span class="legend-item"><span class="dot completed"></span> 已完成</span>
            <span class="legend-item"><span class="dot total"></span> 总任务</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 超期任务提醒 -->
    <div class="overdue-panel">
      <div class="panel-title">
        <h3>超期任务提醒</h3>
      </div>
      <div v-if="!overdueTasks.length" class="empty-state success">
        <div class="empty-illustration">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <p>没有超期任务，继续保持！</p>
      </div>
      <div v-else class="overdue-grid">
        <div
          v-for="(task, index) in overdueTasks"
          :key="task.id"
          class="overdue-item"
          :style="{ animationDelay: `${index * 0.06}s` }"
        >
          <div class="overdue-header">
            <div class="plan-badge" :style="{ background: `${task.planColor}20`, color: task.planColor }">
              {{ task.planName }}
            </div>
            <div class="overdue-days">
              超期 {{ getOverdueDays(task.deadline) }} 天
            </div>
          </div>
          <div class="overdue-body">
            <h4 class="overdue-name">{{ task.taskName }}</h4>
            <div class="overdue-meta">
              <span class="meta-item">
                <el-icon><Calendar /></el-icon>
                {{ formatDate(task.deadline) }}
              </span>
              <span class="status-tag" :class="`status-${task.status.toLowerCase()}`">
                {{ getStatusText(task.status) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getDashboard } from '@/api/statistics'
import { getOverdueTasks } from '@/api/task'
import { formatDate, formatMinutes, getStatusText } from '@/utils'
import dayjs from 'dayjs'

const dashboard = ref(null)
const overdueTasks = ref([])

// 本周日期数组
const weekDays = computed(() => {
  const days = []
  for (let i = 6; i >= 0; i--) {
    days.push(dayjs().subtract(i, 'day').format('YYYY-MM-DD'))
  }
  return days
})

const getWeeklyDayStat = (date) => {
  return dashboard.value?.weeklyTrend?.find(d => d.date === date)
}

const formatDayLabel = (date) => {
  const d = dayjs(date)
  const today = dayjs()
  if (d.isSame(today, 'day')) return '今天'
  if (d.isSame(today.subtract(1, 'day'), 'day')) return '昨天'
  return d.format('MM/DD')
}

const getBarWidth = (count) => {
  const maxCount = 10
  const percentage = Math.min((count / maxCount) * 100, 100)
  return `${Math.max(percentage, 5)}%`
}

const getOverdueDays = (deadline) => {
  return dayjs().diff(dayjs(deadline), 'day')
}

const fetchData = async () => {
  try {
    const [dashboardRes, overdueRes] = await Promise.all([
      getDashboard(),
      getOverdueTasks()
    ])
    dashboard.value = dashboardRes.data
    overdueTasks.value = overdueRes.data || []
  } catch (error) {
    console.error('获取数据失败', error)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
/* ═════════════════════════════════════════════════════════════
   Stats Grid
   ═════════════════════════════════════════════════════════════ */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  border-radius: var(--radius-lg);
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: var(--shadow-md);
  transition: all var(--transition-base);
  animation: fadeInUp 0.5s var(--ease-smooth);

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-float);
  }

  &.stat-blue {
    background: var(--gradient-blue-ink);
    border: none;
  }

  &.stat-green {
    background: var(--gradient-sage);
    border: none;
  }

  &.stat-sage {
    background: var(--gradient-sage);
    border: none;
  }

  &.stat-orange {
    background: var(--gradient-cinnabar);
    border: none;
  }

  .stat-icon {
    font-size: 32px;
    color: var(--paper-white);
    opacity: 0.9;
  }

  .stat-content {
    flex: 1;
  }

  .stat-value {
    font-size: 36px;
    font-family: var(--font-display);
    font-weight: 700;
    color: var(--paper-white);
    letter-spacing: -0.02em;
    line-height: 1;
    margin-bottom: 4px;
  }

  .stat-label {
    font-size: 13px;
    color: rgba(245, 242, 234, 0.85);
    letter-spacing: 0.08em;
  }
}

/* ═════════════════════════════════════════════════════════════
   Content Row
   ═════════════════════════════════════════════════════════════ */
.content-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

/* ═════════════════════════════════════════════════════════════
   Panel
   ═════════════════════════════════════════════════════════════ */
.panel-panel {
  background: rgba(255, 255, 255, 0.6);
  border-radius: var(--radius-xl);
  padding: 24px;
  box-shadow: var(--shadow-md);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.panel-title {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(26, 29, 35, 0.06);

  h3 {
    font-family: var(--font-display);
    font-size: 18px;
    font-weight: 600;
    color: var(--ink-deep);
    letter-spacing: 0.05em;
    margin: 0;
  }
}

/* ═════════════════════════════════════════════════════════════
   Empty State
   ═════════════════════════════════════════════════════════════ */
.empty-state {
  text-align: center;
  padding: 48px 24px;
  color: var(--muted);
  position: relative;
  border: 2px dashed rgba(124, 139, 125, 0.2);
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.4), rgba(255, 255, 255, 0.2));

  &::before {
    content: '';
    position: absolute;
    width: 200px;
    height: 200px;
    background: radial-gradient(circle, rgba(124, 139, 125, 0.06), transparent 60%);
    top: -80px;
    left: -40px;
    filter: blur(1px);
  }

  &.success {
    border-color: rgba(124, 139, 125, 0.3);
    background: linear-gradient(135deg, rgba(124, 139, 125, 0.1), rgba(124, 139, 125, 0.05));
  }

  p {
    font-size: 14px;
    letter-spacing: 0.05em;
  }
}

.empty-illustration {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(124, 139, 125, 0.16), rgba(124, 139, 125, 0.08));
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;

  .el-icon {
    font-size: 28px;
    color: var(--sage);
  }
}

/* ═════════════════════════════════════════════════════════════
   Plan Stats
   ═════════════════════════════════════════════════════════════ */
.plan-stats-list {
  max-height: 380px;
  overflow-y: auto;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(74, 80, 92, 0.15);
    border-radius: var(--radius-pill);
  }
}

.plan-stat-item {
  padding: 16px 0;
  border-bottom: 1px solid rgba(26, 29, 35, 0.06);
  animation: fadeIn 0.4s var(--ease-smooth);

  &:last-child {
    border-bottom: none;
  }
}

.plan-stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.plan-info {
  display: flex;
  align-items: center;
}

.color-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 10px;
  box-shadow: 0 0 0 3px rgba(0, 0, 0, 0.05);
}

.plan-name {
  color: var(--ink-deep);
  font-size: 14px;
}

.plan-rate {
  font-family: var(--font-mono);
  font-weight: 600;
  font-size: 16px;
  color: var(--ink-deep);
}

.plan-stat-progress {
  margin-bottom: 10px;
}

.progress-track {
  height: 8px;
  background: rgba(26, 29, 35, 0.08);
  border-radius: var(--radius-pill);
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  border-radius: var(--radius-pill);
  transition: width 0.6s var(--ease-smooth);
}

.plan-stat-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: var(--muted);
}

.time-info {
  color: var(--muted);
}

/* ═════════════════════════════════════════════════════════════
   Weekly Chart
   ═════════════════════════════════════════════════════════════ */
.weekly-chart {
  padding: 20px 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.day-bar {
  display: grid;
  grid-template-columns: 80px 100px 1fr 1fr;
  gap: 12px;
  align-items: center;
}

.day-label {
  font-size: 13px;
  color: var(--muted);
  font-family: var(--font-mono);
  text-align: center;
}

.day-stats {
  font-size: 12px;
  color: var(--muted);
  text-align: right;
}

.bar-container {
  display: flex;
  align-items: center;
}

.bar-track {
  flex: 1;
  height: 10px;
  background: rgba(26, 29, 35, 0.06);
  border-radius: var(--radius-pill);
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: var(--radius-pill);
  transition: width 0.4s var(--ease-smooth);
}

.bar-fill.total {
  background: rgba(203, 213, 224, 0.3);
}

.bar-fill.completed {
  background: var(--sage);
}

.chart-legend {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid rgba(26, 29, 35, 0.06);
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--muted);
}

.legend-item .dot {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-item .dot.completed {
  background: var(--sage);
}

.legend-item .dot.total {
  background: rgba(203, 213, 224, 0.3);
}

/* ═════════════════════════════════════════════════════════════
   Overdue Panel
   ═════════════════════════════════════════════════════════════ */
.overdue-panel {
  background: rgba(255, 255, 255, 0.6);
  border-radius: var(--radius-xl);
  padding: 24px;
  box-shadow: var(--shadow-md);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.overdue-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
  max-height: 320px;
  overflow-y: auto;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(74, 80, 92, 0.15);
    border-radius: var(--radius-pill);
  }
}

.overdue-item {
  background: rgba(255, 255, 255, 0.5);
  border-radius: var(--radius-lg);
  padding: 16px;
  border-left: 3px solid var(--cinnabar);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-base);
  animation: fadeInUp 0.4s var(--ease-smooth);

  &:hover {
    transform: translateX(4px);
    box-shadow: var(--shadow-md);
  }
}

.overdue-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.plan-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: var(--radius-pill);
  font-size: 11px;
  letter-spacing: 0.05em;
  font-weight: 500;
}

.overdue-days {
  font-size: 12px;
  color: var(--cinnabar);
  font-weight: 500;
  letter-spacing: 0.05em;
}

.overdue-body {
  margin-bottom: 12px;
}

.overdue-name {
  font-family: var(--font-display);
  font-size: 15px;
  font-weight: 600;
  color: var(--ink-deep);
  letter-spacing: 0.05em;
  margin: 0 0 8px;
}

.overdue-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--muted);

  .el-icon {
    font-size: 13px;
  }
}

.status-tag {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: var(--radius-pill);
  letter-spacing: 0.05em;
  font-weight: 500;

  &.status-not_started {
    background: rgba(154, 163, 168, 0.15);
    color: var(--muted);
  }

  &.status-in_progress {
    background: rgba(61, 90, 128, 0.15);
    color: var(--ink-light);
  }

  &.status-completed {
    background: rgba(124, 139, 125, 0.2);
    color: var(--sage);
  }

  &.status-delayed {
    background: rgba(196, 80, 80, 0.15);
    color: var(--cinnabar);
  }
}

/* ═════════════════════════════════════════════════════════════
   Animations
   ═════════════════════════════════════════════════════════════ */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style>
