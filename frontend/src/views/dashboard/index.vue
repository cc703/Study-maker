<template>
  <div class="dashboard">
    <!-- 统计卡片 - 优化版 -->
    <div class="stats-grid">
      <!-- 今日任务卡片 -->
      <div class="stat-card stat-blue" style="animation-delay: 0.02s;" @click="goToTask">
        <div class="stat-header">
          <div class="stat-icon-wrapper">
            <el-icon><Calendar /></el-icon>
          </div>
          <div class="trend-icon">
            <el-icon><TrendCharts /></el-icon>
          </div>
        </div>
        <div class="stat-body">
          <div class="stat-value-large">{{ dashboard?.todayTotal || 0 }}</div>
          <div class="stat-label">今日任务</div>
          <div class="stat-progress">
            <div class="progress-ring">
              <div
                class="progress-fill"
                :style="{
                  background: 'conic-gradient(from 180deg, var(--paper-white) 0%, var(--paper-white) ' + (dashboard?.todayCompletionRate || 0) + '%, transparent ' + (dashboard?.todayCompletionRate || 0) + '%)',
                  clipPath: 'circle(50%, 50%, 40%, 100%, 100%, 50%, 50%, 100%, 50%, 50%)'
                }"
              ></div>
              <div class="progress-inner">
                <span class="progress-text">{{ dashboard?.todayCompletionRate || 0 }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 已完成卡片 -->
      <div class="stat-card stat-green" style="animation-delay: 0.08s;">
        <div class="stat-header">
          <div class="stat-icon-wrapper">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="trend-icon trend-up">
            <el-icon><CaretTop /></el-icon>
          </div>
        </div>
        <div class="stat-body">
          <div class="stat-value-large">{{ dashboard?.todayCompleted || 0 }}</div>
          <div class="stat-label">已完成</div>
          <div class="stat-hint">
            <span class="hint-label">完成率</span>
            <span class="hint-value">{{ dashboard?.todayCompletionRate || 0 }}%</span>
          </div>
        </div>
      </div>

      <!-- 进行中卡片 -->
      <div class="stat-card stat-orange" style="animation-delay: 0.14s;">
        <div class="stat-header">
          <div class="stat-icon-wrapper">
            <el-icon><Timer /></el-icon>
          </div>
          <div class="trend-icon trend-down">
            <el-icon><CaretBottom /></el-icon>
          </div>
        </div>
        <div class="stat-body">
          <div class="stat-value-large">{{ dashboard?.todayInProgress || 0 }}</div>
          <div class="stat-label">进行中</div>
          <div class="stat-hint">
            <span class="hint-label">待处理</span>
            <span class="hint-value">{{ dashboard?.todayTotal - dashboard?.todayCompleted - dashboard?.todayInProgress || 0 }}</span>
          </div>
        </div>
      </div>

      <!-- 连续学习天数卡片 -->
      <div class="stat-card stat-purple" style="animation-delay: 0.20s;">
        <div class="stat-header">
          <div class="stat-icon-wrapper large">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="trend-badge"> streak</div>
        </div>
        <div class="stat-body">
          <div class="stat-value-extra-large">{{ dashboard?.consecutiveDays || 0 }}</div>
          <div class="stat-label">连续学习天数</div>
          <div class="stat-hint">
            <span class="hint-label">目标</span>
            <span class="hint-value">30天</span>
          </div>
          <div class="streak-bar">
            <div
              class="streak-fill"
              :style="{ width: Math.min((dashboard?.consecutiveDays || 0) / 30 * 100, 100) + '%' }"
            ></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 今日任务列表 -->
    <div class="main-content">
      <el-row :gutter="20">
        <!-- 今日任务 -->
        <el-col :span="14">
          <div class="page-card" style="animation-delay: 0.26s;">
            <div class="card-header">
              <div class="card-title-wrapper">
                <span class="card-title">今日任务</span>
                <span class="card-subtitle">{{ todayTasks.length }} 项</span>
              </div>
              <button class="btn-add" @click="goToTask">
                <el-icon><Plus /></el-icon>
                <span>添加任务</span>
              </button>
            </div>

            <div v-if="todayTasks.length === 0" class="empty-state">
              <div class="empty-illustration">
                <el-icon><Document /></el-icon>
              </div>
              <p>今日暂无任务</p>
              <p class="empty-hint">点击右侧按钮创建新任务</p>
            </div>

            <div v-else class="task-list">
              <div
                v-for="(task, index) in todayTasks"
                :key="task.id"
                class="task-item"
                :class="{ completed: task.status === 'COMPLETED' }"
                :style="{ animationDelay: `${0.3 + index * 0.05}s` }"
              >
                <div class="task-main">
                  <div class="task-plan-tag" :style="{ background: `${task.planColor}20`, color: task.planColor }">
                    {{ task.planName }}
                  </div>
                  <div class="task-name">{{ task.taskName }}</div>
                </div>
                <div class="task-actions">
                  <span class="task-status" :class="`status-${task.status.toLowerCase()}`">
                    {{ getStatusText(task.status) }}
                  </span>
                  <button
                    v-if="task.status === 'NOT_STARTED'"
                    class="btn-action btn-start"
                    @click="handleStart(task)"
                  >
                    开始
                  </button>
                  <button
                    v-if="task.status === 'IN_PROGRESS'"
                    class="btn-action btn-complete"
                    @click="handleComplete(task)"
                  >
                    完成
                  </button>
                </div>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 学习方向完成率 -->
        <el-col :span="10">
          <div class="page-card" style="animation-delay: 0.32s;">
            <div class="card-title">各方向完成率</div>

            <div v-if="!dashboard?.planStats?.length" class="empty-state">
              <div class="empty-illustration">
                <el-icon><PieChart /></el-icon>
              </div>
              <p>暂无统计数据</p>
            </div>

            <div v-else class="plan-stats">
              <div
                v-for="(stat, index) in dashboard.planStats"
                :key="stat.planId"
                class="plan-stat-item"
                :style="{ animationDelay: `${0.38 + index * 0.06}s` }"
              >
                <div class="plan-stat-header">
                  <div class="plan-info">
                    <div class="color-dot" :style="{ backgroundColor: stat.color }"></div>
                    <span class="plan-name">{{ stat.planName }}</span>
                  </div>
                  <div class="plan-rate">{{ stat.completionRate || 0 }}%</div>
                </div>

                <div class="plan-progress">
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
        </el-col>
      </el-row>
    </div>

    <!-- 完成任务弹窗 -->
    <el-dialog v-model="completeDialogVisible" title="完成任务" width="400px" custom-class="zen-dialog">
      <div class="complete-form">
        <div class="form-row">
          <label class="form-label">任务名称</label>
          <div class="form-value">{{ currentTask?.taskName }}</div>
        </div>
        <div class="form-row">
          <label class="form-label">实际用时</label>
          <div class="time-input-group">
            <el-input-number v-model="actualTime" :min="1" :max="480" />
            <span class="time-unit">分钟</span>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="completeDialogVisible = false">取消</button>
          <button class="btn-confirm" @click="confirmComplete">确认完成</button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CaretTop, CaretBottom } from '@element-plus/icons-vue'
import { getDashboard } from '@/api/statistics'
import { getTodayTasks, startTask, completeTask } from '@/api/task'
import { getStatusText, formatMinutes } from '@/utils'

const router = useRouter()
const dashboard = ref(null)
const todayTasks = ref([])
const completeDialogVisible = ref(false)
const currentTask = ref(null)
const actualTime = ref(60)

const fetchData = async () => {
  try {
    const [dashboardRes, tasksRes] = await Promise.all([
      getDashboard(),
      getTodayTasks()
    ])
    dashboard.value = dashboardRes.data
    todayTasks.value = tasksRes.data || []
  } catch (error) {
    console.error('获取数据失败', error)
  }
}

const goToTask = () => {
  router.push('/task')
}

const handleStart = async (task) => {
  try {
    await startTask(task.id)
    ElMessage.success('任务已开始')
    fetchData()
  } catch (error) {
    console.error('开始任务失败', error)
  }
}

const handleComplete = (task) => {
  currentTask.value = task
  actualTime.value = task.estimatedTime || 60
  completeDialogVisible.value = true
}

const confirmComplete = async () => {
  try {
    await completeTask(currentTask.value.id, actualTime.value)
    ElMessage.success('任务已完成')
    completeDialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('完成任务失败', error)
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
  border-radius: var(--radius-lg);
  padding: 24px;
  color: var(--paper-white);
  position: relative;
  overflow: hidden;
  box-shadow: var(--shadow-ink);
  backdrop-filter: blur(10px);
  animation: fadeInUp 0.5s var(--ease-smooth);
  transition: all var(--transition-base);
  display: flex;
  align-items: center;
  gap: 16px;

  /* 装饰圆圈 */
  &::before {
    content: '';
    position: absolute;
    width: 140px;
    height: 140px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.12) 0%, transparent 70%);
    top: -50px;
    right: -50px;
  }

  &::after {
    content: '';
    position: absolute;
    width: 70px;
    height: 70px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.08) 0%, transparent 70%);
    bottom: -25px;
    left: -25px;
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-float);
  }

  &.stat-blue {
    background: var(--gradient-blue-ink);
  }

  &.stat-green {
    background: var(--gradient-sage);
  }

  &.stat-orange {
    background: var(--gradient-cinnabar);
  }

  &.stat-purple {
    background: linear-gradient(135deg, #6b5b95 0%, #8b7ab8 100%);
  }
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  position: relative;
  z-index: 1;
}

.stat-icon-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;

  .el-icon {
    font-size: 32px;
    opacity: 0.9;
  }

  &.large {
    .el-icon {
      font-size: 36px;
    }
  }
}

.trend-icon {
  font-size: 20px;
  opacity: 0.6;

  &.trend-up {
    color: rgba(124, 139, 125, 0.8);
  }

  &.trend-down {
    color: rgba(212, 168, 85, 0.6);
  }
}

.trend-badge {
  font-family: var(--font-mono);
  font-size: 10px;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  opacity: 0.5;
  font-weight: 600;
}

.stat-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  z-index: 1;
}

.stat-value-large {
  font-size: 52px;
  font-family: var(--font-display);
  font-weight: 700;
  line-height: 1;
  letter-spacing: -0.02em;
  margin-bottom: 8px;
}

.stat-value-extra-large {
  font-size: 64px;
  font-family: var(--font-display);
  font-weight: 700;
  line-height: 1;
  letter-spacing: -0.02em;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  font-family: var(--font-body);
  opacity: 0.9;
  letter-spacing: 0.08em;
  margin-bottom: 12px;
}

.stat-progress {
  width: 60px;
  height: 60px;
  margin: 0 auto;
  position: relative;
}

.progress-ring {
  position: relative;
  width: 60px;
  height: 60px;
}

.progress-fill {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  transition: all 0.6s var(--ease-smooth);
}

.progress-inner {
  position: absolute;
  inset: 4px;
  border-radius: 50%;
  background: rgba(26, 29, 35, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
}

.progress-text {
  font-family: var(--font-mono);
  font-size: 14px;
  font-weight: 600;
}

.stat-hint {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: var(--radius-md);
  font-size: 12px;
}

.hint-label {
  color: var(--muted);
  font-family: var(--font-body);
}

.hint-value {
  font-family: var(--font-mono);
  font-weight: 600;
  color: var(--paper-white);
}

.streak-bar {
  width: 100%;
  height: 4px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-pill);
  overflow: hidden;
  margin-top: 12px;
}

.streak-fill {
  height: 100%;
  background: var(--gold-subtle);
  border-radius: var(--radius-pill);
  transition: width 0.6s var(--ease-smooth);
}

/* ═════════════════════════════════════════════════════════════
   Main Content
   ═══════════════════════════════════════════════════════════ */
.main-content {
  display: grid;
  grid-template-columns: 14fr 10fr;
  gap: 20px;
}

/* ═════════════════════════════════════════════════════════════
   Page Card Header
   ═════════════════════════════════════════════════════════════ */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(26, 29, 35, 0.06);
}

.card-title-wrapper {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.card-title {
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 600;
  color: var(--ink-deep);
  letter-spacing: 0.05em;
}

.card-subtitle {
  font-size: 13px;
  color: var(--muted);
  font-family: var(--font-mono);
}

/* ═════════════════════════════════════════════════════════════
   Buttons
   ═════════════════════════════════════════════════════════════ */
.btn-add {
  display: flex;
  align-items: center;
  gap: 6px;
  background: var(--gradient-ink);
  color: var(--paper-white);
  border: none;
  border-radius: var(--radius-md);
  padding: 10px 20px;
  font-size: 14px;
  letter-spacing: 0.05em;
  cursor: pointer;
  transition: all var(--transition-base);
  box-shadow: var(--shadow-sm);

  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-md);
  }

  .el-icon {
    font-size: 16px;
  }
}

/* ═════════════════════════════════════════════════════════════
   Empty State
   ═════════════════════════════════════════════════════════════ */
.empty-state {
  text-align: center;
  padding: 60px 24px;
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
    left: -60px;
    filter: blur(1px);
  }

  p {
    font-size: 14px;
    letter-spacing: 0.05em;
  }

  .empty-hint {
    font-size: 12px;
    color: var(--muted);
    margin-top: 8px;
  }
}

.empty-illustration {
  width: 72px;
  height: 72px;
  margin: 0 auto 16px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(124, 139, 125, 0.16), rgba(124, 139, 125, 0.08));
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;

  .el-icon {
    font-size: 32px;
    color: var(--sage);
  }
}

/* ═════════════════════════════════════════════════════════════
   Task List
   ═════════════════════════════════════════════════════════════ */
.task-list {
  max-height: 480px;
  overflow-y: auto;
  padding-right: 4px;

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

.task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-radius: var(--radius-lg);
  margin-bottom: 12px;
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.7);
  transition: all var(--transition-base);
  animation: fadeIn 0.4s var(--ease-smooth);
  position: relative;

  &:hover {
    background: rgba(255, 255, 255, 0.9);
    border-color: var(--sage);
    transform: translateX(4px);
    box-shadow: var(--shadow-sm);
  }

  &.completed {
    opacity: 0.5;
  }
}

.task-main {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.task-plan-tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: var(--radius-pill);
  font-size: 11px;
  letter-spacing: 0.05em;
  align-self: flex-start;
}

.task-name {
  color: var(--ink-deep);
  font-size: 14px;
  letter-spacing: 0.02em;
}

.task-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.task-status {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: var(--radius-pill);
  letter-spacing: 0.05em;
  font-family: var(--font-body);

  &.status-not_started {
    background: rgba(154, 163, 168, 0.12);
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
}

.btn-action {
  padding: 6px 14px;
  border-radius: var(--radius-md);
  font-size: 12px;
  letter-spacing: 0.05em;
  border: none;
  cursor: pointer;
  transition: all var(--transition-base);

  &.btn-start {
    background: var(--gradient-blue-ink);
    color: var(--paper-white);

    &:hover {
      transform: translateY(-1px);
      box-shadow: var(--shadow-sm);
    }
  }

  &.btn-complete {
    background: var(--gradient-sage);
    color: var(--paper-white);

    &:hover {
      transform: translateY(-1px);
      box-shadow: var(--shadow-sm);
    }
  }
}

/* ═════════════════════════════════════════════════════════════
   Plan Stats
   ═══════════════════════════════════════════════════════════ */
.card-title {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 600;
  color: var(--ink-deep);
  letter-spacing: 0.05em;
  margin-bottom: 20px;
}

.plan-stats {
  max-height: 480px;
  overflow-y: auto;
  padding-right: 4px;

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
  margin-bottom: 10px;
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
  font-family: var(--font-body);
}

.plan-rate {
  color: var(--ink-deep);
  font-family: var(--font-mono);
  font-weight: 600;
  font-size: 18px;
}

.plan-progress {
  margin-bottom: 8px;
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

/* ═══════════════════════════════════════════════════════════
   Dialog
   ═══════════════════════════════════════════════════════════════ */
.complete-form {
  padding: 12px 0;
}

.form-row {
  margin-bottom: 20px;

  &:last-child {
    margin-bottom: 0;
  }
}

.form-label {
  display: block;
  font-size: 13px;
  color: var(--muted);
  margin-bottom: 8px;
  letter-spacing: 0.05em;
}

.form-value {
  font-size: 15px;
  color: var(--ink-deep);
  padding: 10px 14px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: var(--radius-md);
}

.time-input-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.time-unit {
  font-size: 13px;
  color: var(--muted);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 8px;
}

.btn-cancel {
  padding: 10px 24px;
  border: 1px solid rgba(26, 29, 35, 0.15);
  border-radius: var(--radius-md);
  background: transparent;
  color: var(--ink-medium);
  font-size: 14px;
  letter-spacing: 0.05em;
  cursor: pointer;
  transition: all var(--transition-base);

  &:hover {
    background: rgba(26, 29, 35, 0.04);
  }
}

.btn-confirm {
  padding: 10px 24px;
  border: none;
  border-radius: var(--radius-md);
  background: var(--gradient-ink);
  color: var(--paper-white);
  font-size: 14px;
  letter-spacing: 0.05em;
  cursor: pointer;
  transition: all var(--transition-base);
  box-shadow: var(--shadow-sm);

  &:hover {
    transform: translateY(-1px);
    box-shadow: var(--shadow-md);
  }
}

/* ═══════════════════════════════════════════════════════════════
   Animations
   ═══════════════════════════════════════════════════════════════ */
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
