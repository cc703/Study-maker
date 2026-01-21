<template>
  <div class="year-calendar-wrapper">
    <div class="calendar-header">
      <span class="calendar-title">全年日历</span>
      <el-select v-model="selectedYear" size="small" class="year-select">
        <el-option
          v-for="year in yearOptions"
          :key="year"
          :label="year + '年'"
          :value="year"
        />
      </el-select>
    </div>
    <div v-if="!yearlyStats.length" class="empty-state">
      <el-icon><Calendar /></el-icon>
      <p>暂无全年数据</p>
    </div>
    <div v-else class="year-calendar">
      <div v-for="month in calendarMonths" :key="month.monthIndex" class="month-card">
        <div class="month-title">{{ month.label }}</div>
        <div class="month-grid">
          <div class="week-row" v-for="(week, wIndex) in month.weeks" :key="wIndex">
            <div
              v-for="day in week"
              :key="day.dateStr"
              class="day-cell"
              :class="[day.inMonth ? getLevelClass(day.dateStr) : 'out-month']"
            >
              <el-tooltip v-if="day.inMonth" placement="top" :show-after="300">
                <template #content>
                  <div class="tooltip-content">
                    <div>{{ day.dateStr }}</div>
                    <div>完成 {{ getYearlyDayStat(day.dateStr)?.completedTasks || 0 }} / {{ getYearlyDayStat(day.dateStr)?.totalTasks || 0 }}</div>
                  </div>
                </template>
                <span class="day-number">{{ day.dayOfMonth }}</span>
              </el-tooltip>
              <span v-else class="day-number"></span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="calendar-legend">
      <span class="legend-text">完成任务</span>
      <span class="legend-block level-0"></span>
      <span class="legend-block level-1"></span>
      <span class="legend-block level-2"></span>
      <span class="legend-block level-3"></span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import dayjs from 'dayjs'
import { getYearlyStats } from '@/api/statistics'

const yearlyStats = ref([])
const selectedYear = ref(dayjs().year())

const yearOptions = computed(() => {
  const current = dayjs().year()
  return [current - 2, current - 1, current, current + 1, current + 2]
})

const yearlyStatMap = computed(() => {
  const map = new Map()
  yearlyStats.value.forEach(item => {
    const key = dayjs(item.date).format('YYYY-MM-DD')
    map.set(key, item)
  })
  return map
})

const getYearlyDayStat = (dateStr) => {
  return yearlyStatMap.value.get(dateStr)
}

const getLevelClass = (dateStr) => {
  const completed = getYearlyDayStat(dateStr)?.completedTasks || 0
  if (completed <= 0) return 'level-0'
  if (completed <= 2) return 'level-1'
  if (completed <= 4) return 'level-2'
  return 'level-3'
}

const calendarMonths = computed(() => {
  const months = []
  for (let m = 0; m < 12; m++) {
    const monthStart = dayjs().year(selectedYear.value).month(m).startOf('month')
    const monthEnd = dayjs().year(selectedYear.value).month(m).endOf('month')
    let cursor = monthStart.startOf('week')
    const endCursor = monthEnd.endOf('week')
    const weeks = []
    while (cursor.isBefore(endCursor) || cursor.isSame(endCursor, 'day')) {
      const week = []
      for (let i = 0; i < 7; i++) {
        week.push({
          dateStr: cursor.format('YYYY-MM-DD'),
          dayOfMonth: cursor.date(),
          inMonth: cursor.month() === m
        })
        cursor = cursor.add(1, 'day')
      }
      weeks.push(week)
    }
    months.push({
      monthIndex: m,
      label: monthStart.format('M月'),
      weeks
    })
  }
  return months
})

const fetchYearlyStats = async () => {
  try {
    const res = await getYearlyStats(selectedYear.value)
    yearlyStats.value = res.data || []
  } catch (error) {
    console.error('获取全年数据失败', error)
  }
}

watch(selectedYear, () => {
  fetchYearlyStats()
})

onMounted(() => {
  fetchYearlyStats()
})
</script>

<style scoped>
.year-calendar-wrapper {
  min-width: 720px;
}

.calendar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.calendar-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.year-select {
  width: 120px;
}

.year-calendar {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.month-card {
  background: #f8f9fb;
  border-radius: 8px;
  padding: 10px;
  border: 1px solid #ebeef5;
}

.month-title {
  font-size: 12px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
}

.month-grid {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.week-row {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 3px;
}

.day-cell {
  width: 100%;
  padding-top: 100%;
  position: relative;
  border-radius: 4px;
  background: #f0f2f5;
}

.day-cell.out-month {
  background: transparent;
}

.day-number {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 10px;
  color: #606266;
}

.level-0 {
  background: #f0f2f5;
}

.level-1 {
  background: #d9ecff;
}

.level-2 {
  background: #a0cfff;
}

.level-3 {
  background: #409eff;
}

.calendar-legend {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  font-size: 12px;
  color: #606266;
}

.legend-text {
  margin-right: 4px;
}

.legend-block {
  width: 14px;
  height: 14px;
  border-radius: 3px;
  display: inline-block;
}

.empty-state {
  text-align: center;
  padding: 24px 0;
  color: #909399;
}

.empty-state .el-icon {
  font-size: 32px;
  margin-bottom: 8px;
}
</style>
