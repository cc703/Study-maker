<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <aside class="layout-aside">
      <div class="logo">
        <el-icon><Calendar /></el-icon>
        <span>墨韵学习</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>看看自己的数据趴</span>
        </el-menu-item>
        <el-menu-item index="/plan">
          <el-icon><Flag /></el-icon>
          <span>好好看看该学啥</span>
        </el-menu-item>
        <el-menu-item index="/stage">
          <el-icon><Medal /></el-icon>
          <span>定一个目标吧</span>
        </el-menu-item>
        <el-menu-item index="/task">
          <el-icon><List /></el-icon>
          <span>看看你干了啥</span>
        </el-menu-item>
        <el-menu-item index="/statistics">
          <el-icon><DataAnalysis /></el-icon>
          <span>到现在你干了啥</span>
        </el-menu-item>
        <el-menu-item index="/memo">
          <el-icon><Document /></el-icon>
          <span>记一下那无聊的生活吧</span>
        </el-menu-item>
      </el-menu>

      <!-- 返回欢迎页 -->
      <div class="welcome-link">
        <router-link to="/welcome" class="welcome-link-btn">
          <el-icon><HomeFilled /></el-icon>
          <span>返回首页</span>
        </router-link>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="layout-main">
      <!-- 顶部栏 -->
      <header class="layout-header">
        <div class="header-left">
          <span>{{ currentTitle }}</span>
        </div>
        <div class="header-right">
          <el-date-picker
            v-model="selectedDate"
            type="date"
            size="small"
            format="YYYY年MM月DD日"
            value-format="YYYY-MM-DD"
            :editable="false"
          />
          <span class="weekday-text">{{ selectedWeekday }}</span>
          <el-button size="small" @click="settingsVisible = true">提醒设置</el-button>
          <el-popover placement="bottom-end" trigger="click" width="760">
            <template #reference>
              <el-button size="small" plain>全年日历</el-button>
            </template>
            <YearCalendar />
          </el-popover>
        </div>
      </header>

      <!-- 内容区 -->
      <main class="layout-content">
        <router-view />
      </main>
    </div>
  </div>

  <el-dialog v-model="settingsVisible" title="提醒设置" width="420px">
    <el-form label-width="100px">
      <el-form-item label="开启提醒">
        <el-switch v-model="notifyEnabled" />
      </el-form-item>
      <el-form-item label="提醒时间">
        <el-time-picker
          v-model="notifyTime"
          value-format="HH:mm"
          format="HH:mm"
          :disabled="!notifyEnabled"
          placeholder="选择时间"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="settingsVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { HomeFilled } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'
import dayjs from 'dayjs'
import YearCalendar from '@/components/YearCalendar.vue'
import { getOverdueTasks, getTasksByDeadline } from '@/api/task'

const route = useRoute()

const activeMenu = computed(() => route.path)

const currentTitle = computed(() => {
  const titles = {
    '/dashboard': '仪表盘',
    '/plan': '学习方向管理',
    '/stage': '阶段目标管理',
    '/task': '任务管理',
    '/statistics': '统计与复盘',
    '/memo': '备忘录'
  }
  return titles[route.path] || '学习任务规划系统'
})

const selectedDate = ref(dayjs().format('YYYY-MM-DD'))
const settingsVisible = ref(false)
const notifyEnabled = ref(true)
const notifyTime = ref('09:00')
const notifyTimerId = ref(null)

const selectedWeekday = computed(() => {
  if (!selectedDate.value) return ''
  return dayjs(selectedDate.value).format('dddd')
})

const loadNotifySettings = () => {
  const enabled = localStorage.getItem('lp_notify_enabled')
  const time = localStorage.getItem('lp_notify_time')
  if (enabled !== null) {
    notifyEnabled.value = enabled === 'true'
  }
  if (time) {
    notifyTime.value = time
  }
}

const saveNotifySettings = () => {
  localStorage.setItem('lp_notify_enabled', String(notifyEnabled.value))
  localStorage.setItem('lp_notify_time', notifyTime.value)
}

const requestNotificationPermission = async () => {
  if (!('Notification' in window)) return false
  if (Notification.permission === 'granted') return true
  if (Notification.permission === 'denied') return false
  const permission = await Notification.requestPermission()
  return permission === 'granted'
}

const showNotification = (title, body) => {
  if (!('Notification' in window)) return
  if (Notification.permission !== 'granted') return
  new Notification(title, { body })
}

const notifyDueAndOverdue = async () => {
  const today = dayjs().format('YYYY-MM-DD')
  const tomorrow = dayjs().add(1, 'day').format('YYYY-MM-DD')
  const lastNotice = localStorage.getItem('lp_notify_date')
  if (lastNotice === today) return

  const granted = await requestNotificationPermission()
  if (!granted) return

  try {
    const [overdueRes, dueTodayRes, dueTomorrowRes] = await Promise.all([
      getOverdueTasks(),
      getTasksByDeadline(today),
      getTasksByDeadline(tomorrow)
    ])
    const overdueTasks = overdueRes.data || []
    const dueTodayTasks = dueTodayRes.data || []
    const dueTomorrowTasks = dueTomorrowRes.data || []

    if (overdueTasks.length > 0) {
      showNotification('超期任务提醒', `当前有 ${overdueTasks.length} 个任务已超期，请及时处理。`)
    }
    if (dueTodayTasks.length > 0) {
      showNotification('到期任务提醒', `今天有 ${dueTodayTasks.length} 个任务到期，记得完成。`)
    }
    if (dueTomorrowTasks.length > 0) {
      showNotification('明日到期提醒', `明天有 ${dueTomorrowTasks.length} 个任务到期，建议提前安排。`)
    }

    if (overdueTasks.length > 0 || dueTodayTasks.length > 0 || dueTomorrowTasks.length > 0) {
      localStorage.setItem('lp_notify_date', today)
    }
  } catch (error) {
    console.error('通知提醒获取失败', error)
  }
}

const getNextNotifyDelay = () => {
  if (!notifyTime.value) return null
  const [hour, minute] = notifyTime.value.split(':').map(Number)
  if (Number.isNaN(hour) || Number.isNaN(minute)) return null
  const now = dayjs()
  let target = now.hour(hour).minute(minute).second(0)
  if (target.isBefore(now)) {
    target = target.add(1, 'day')
  }
  return target.diff(now, 'millisecond')
}

const scheduleNotifications = () => {
  if (notifyTimerId.value) {
    clearTimeout(notifyTimerId.value)
  }
  if (!notifyEnabled.value) return
  const delay = getNextNotifyDelay()
  if (delay === null) return

  notifyTimerId.value = setTimeout(async () => {
    await notifyDueAndOverdue()
    scheduleNotifications()
  }, delay)
}

onMounted(() => {
  loadNotifySettings()
  scheduleNotifications()
})

watch([notifyEnabled, notifyTime], () => {
  saveNotifySettings()
  scheduleNotifications()
})
</script>

<style scoped>
/* Layout 样式已移至全局 styles/index.scss */

/* 返回欢迎页样式 */
.welcome-link {
  margin-top: auto;
  padding: 0 16px;
}

.welcome-link-btn {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: var(--radius-md);
  text-decoration: none;
  color: var(--ink-light);
  font-size: 14px;
  letter-spacing: 0.02em;
  transition: all 0.3s var(--ease-fast);
  background: rgba(195, 32, 32, 0.3);
  border: 1px solid rgba(22, 73, 175, 0.08);

  .el-icon {
    font-size: 18px;
    color: var(--sage);
  }

  &:hover {
    background: rgba(255, 255, 255, 0.6);
    color: var(--ink-deep);
    border-color: var(--sage);
    transform: translateY(-2px);
  }
}
</style>
