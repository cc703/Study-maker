<template>
  <div class="task-page">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <div class="search-filters">
        <el-select v-model="queryParams.planId" placeholder="学习方向" clearable class="filter-select">
          <el-option
            v-for="plan in planList"
            :key="plan.id"
            :label="plan.planName"
            :value="plan.id"
          >
            <span class="option-dot" :style="{ backgroundColor: plan.color }"></span>
            {{ plan.planName }}
          </el-option>
        </el-select>
        <el-select v-model="queryParams.status" placeholder="状态" clearable class="filter-select">
          <el-option label="未开始" value="NOT_STARTED" />
          <el-option label="进行中" value="IN_PROGRESS" />
          <el-option label="已完成" value="COMPLETED" />
          <el-option label="延期" value="DELAYED" />
        </el-select>
        <el-select v-model="queryParams.taskType" placeholder="任务类型" clearable class="filter-select">
          <el-option label="学习" value="STUDY" />
          <el-option label="练习" value="PRACTICE" />
          <el-option label="复盘" value="REVIEW" />
          <el-option label="模拟" value="MOCK" />
        </el-select>
        <el-date-picker
          v-model="queryParams.scheduledDate"
          type="date"
          placeholder="计划日期"
          value-format="YYYY-MM-DD"
          class="filter-date"
        />
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索任务名称"
          clearable
          class="filter-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      <div class="search-actions">
        <button class="btn-secondary" @click="resetQuery">重置</button>
        <button class="btn-secondary" @click="exportCsv">CSV</button>
        <button class="btn-secondary" @click="exportExcel">Excel</button>
        <button class="btn-secondary" @click="exportWeeklyReport">周报</button>
        <button class="btn-primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          <span>新增任务</span>
        </button>
      </div>
    </div>

    <!-- 任务卡片列表 -->
    <div v-if="taskList.length === 0" class="empty-state">
      <div class="empty-illustration">
        <el-icon><List /></el-icon>
      </div>
      <p>暂无任务</p>
      <p class="empty-hint">点击上方按钮创建新任务</p>
    </div>

    <div v-else class="task-grid">
      <div
        v-for="(task, index) in taskList"
        :key="task.id"
        class="task-card"
        :class="{
          completed: task.status === 'COMPLETED',
          overdue: isOverdue(task)
        }"
        :style="{ animationDelay: `${index * 0.05}s`, borderLeftColor: task.planColor }"
      >
        <div class="task-header">
          <div class="plan-badge" :style="{ background: `${task.planColor}20`, color: task.planColor }">
            {{ task.planName }}
          </div>
          <div class="task-type">
            {{ getTaskTypeText(task.taskType) }}
          </div>
        </div>

        <div class="task-body">
          <h3 class="task-name">{{ task.taskName }}</h3>
          <p v-if="task.remark" class="task-remark">{{ task.remark }}</p>

          <div class="task-meta">
            <div class="meta-item">
              <el-icon><Calendar /></el-icon>
              <span>{{ formatDate(task.scheduledDate) }}</span>
            </div>
            <div class="meta-item" :class="{ 'meta-danger': isOverdue(task) }">
              <el-icon><Clock /></el-icon>
              <span>{{ formatDate(task.deadline) }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Timer /></el-icon>
              <span>{{ formatMinutes(task.estimatedTime) }}</span>
            </div>
          </div>

          <div class="task-priority">
            <span class="priority-label">优先级</span>
            <div class="priority-dots">
              <span
                v-for="i in 5"
                :key="i"
                class="priority-dot"
                :class="{ active: i <= task.priority }"
              ></span>
            </div>
          </div>
        </div>

        <div class="task-footer">
          <div class="task-status" :class="`status-${task.status.toLowerCase()}`">
            {{ getStatusText(task.status) }}
          </div>
          <div class="task-actions">
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
            <button class="btn-text" @click="handleEdit(task)">编辑</button>
            <button class="btn-text btn-danger" @click="handleDelete(task)">删除</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑任务' : '新增任务'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="task-form"
      >
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="form.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="学习方向" prop="planId">
              <el-select
                v-model="form.planId"
                placeholder="请选择"
                style="width: 100%;"
                @change="handlePlanChange"
              >
                <el-option
                  v-for="plan in planList"
                  :key="plan.id"
                  :label="plan.planName"
                  :value="plan.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属阶段" prop="stageId">
              <el-select v-model="form.stageId" placeholder="请选择" clearable style="width: 100%;">
                <el-option
                  v-for="stage in filteredStages"
                  :key="stage.id"
                  :label="stage.stageName"
                  :value="stage.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="任务类型" prop="taskType">
              <el-select v-model="form.taskType" style="width: 100%;">
                <el-option label="学习" value="STUDY" />
                <el-option label="练习" value="PRACTICE" />
                <el-option label="复盘" value="REVIEW" />
                <el-option label="模拟" value="MOCK" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-rate v-model="form.priority" :max="5" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="计划日期" prop="scheduledDate">
              <el-date-picker
                v-model="form.scheduledDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="截止日期" prop="deadline">
              <el-date-picker
                v-model="form.deadline"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="预计用时" prop="estimatedTime">
              <el-input-number v-model="form.estimatedTime" :min="1" :max="480" style="width: 100%;" />
              <span style="margin-left: 8px; color: var(--muted); font-size: 12px;">分钟</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" style="width: 100%;">
                <el-option label="未开始" value="NOT_STARTED" />
                <el-option label="进行中" value="IN_PROGRESS" />
                <el-option label="已完成" value="COMPLETED" />
                <el-option label="延期" value="DELAYED" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="dialogVisible = false">取消</button>
          <button class="btn-confirm" @click="handleSubmit">确定</button>
        </div>
      </template>
    </el-dialog>

    <!-- 完成任务弹窗 -->
    <el-dialog v-model="completeDialogVisible" title="完成任务" width="400px">
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTaskList, createTask, updateTask, deleteTask, startTask, completeTask } from '@/api/task'
import { getPlanList } from '@/api/plan'
import { getStageList } from '@/api/stage'
import { formatDate, formatMinutes, getStatusText, getTaskTypeText } from '@/utils'
import dayjs from 'dayjs'
import * as XLSX from 'xlsx'

const taskList = ref([])
const planList = ref([])
const stageList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const completeDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const currentTask = ref(null)
const actualTime = ref(60)

const queryParams = ref({
  planId: null,
  stageId: null,
  taskType: null,
  status: null,
  scheduledDate: null,
  keyword: '',
  pageNum: 1,
  pageSize: 10
})

const defaultForm = {
  id: null,
  taskName: '',
  planId: null,
  stageId: null,
  taskType: 'STUDY',
  priority: 3,
  estimatedTime: 60,
  deadline: null,
  status: 'NOT_STARTED',
  actualTime: null,
  remark: '',
  scheduledDate: dayjs().format('YYYY-MM-DD')
}

const form = ref({ ...defaultForm })

const rules = {
  taskName: [
    { required: true, message: '请输入任务名称', trigger: 'blur' },
    { max: 200, message: '名称不能超过200个字符', trigger: 'blur' }
  ],
  planId: [{ required: true, message: '请选择学习方向', trigger: 'change' }]
}

const filteredStages = computed(() => {
  if (!form.value.planId) return []
  return stageList.value.filter(s => s.planId === form.value.planId)
})

const isOverdue = (row) => {
  if (!row.deadline || row.status === 'COMPLETED') return false
  return dayjs(row.deadline).isBefore(dayjs(), 'day')
}

const fetchData = async () => {
  try {
    const res = await getTaskList(queryParams.value)
    taskList.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取数据失败', error)
  }
}

const fetchOptions = async () => {
  try {
    const [planRes, stageRes] = await Promise.all([
      getPlanList(),
      getStageList()
    ])
    planList.value = planRes.data || []
    stageList.value = stageRes.data || []
  } catch (error) {
    console.error('获取选项失败', error)
  }
}

const resetQuery = () => {
  queryParams.value = {
    planId: null,
    stageId: null,
    taskType: null,
    status: null,
    scheduledDate: null,
    keyword: '',
    pageNum: 1,
    pageSize: 10
  }
  fetchData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handlePlanChange = () => {
  form.value.stageId = null
}

const handleStart = async (row) => {
  try {
    await startTask(row.id)
    ElMessage.success('任务已开始')
    fetchData()
  } catch (error) {
    console.error('开始任务失败', error)
  }
}

const handleComplete = (row) => {
  currentTask.value = row
  actualTime.value = row.estimatedTime || 60
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

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该任务吗？', '确认删除', { type: 'warning' })
    .then(async () => {
      try {
        await deleteTask(row.id)
        ElMessage.success('删除成功')
        fetchData()
      } catch (error) {
        console.error('删除失败', error)
      }
    })
    .catch(() => {})
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await updateTask(form.value)
    } else {
      await createTask(form.value)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('提交失败', error)
  }
}

const buildExportParams = (extra = {}) => {
  const { pageNum, pageSize, ...rest } = queryParams.value
  return {
    ...rest,
    pageNum: 1,
    pageSize: 1000,
    ...extra
  }
}

const fetchAllTasks = async (params) => {
  const res = await getTaskList(params)
  return res.data?.list || []
}

const buildExportRows = (tasks) => {
  return tasks.map(task => ({
    任务名称: task.taskName,
    学习方向: task.planName || '',
    类型: getTaskTypeText(task.taskType),
    优先级: task.priority,
    计划日期: formatDate(task.scheduledDate),
    截止日期: formatDate(task.deadline),
    预计用时: formatMinutes(task.estimatedTime),
    状态: getStatusText(task.status),
    备注: task.remark || ''
  }))
}

const downloadBlob = (content, filename, type) => {
  const blob = new Blob([content], { type })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}

const exportCsv = async () => {
  try {
    const list = await fetchAllTasks(buildExportParams())
    const rows = buildExportRows(list)
    if (!rows.length) {
      ElMessage.warning('暂无可导出的任务')
      return
    }
    const headers = Object.keys(rows[0])
    const csvContent = [headers.join(','), ...rows.map(row => headers.map(key => `"${String(row[key] ?? '').replace(/"/g, '""')}"`).join(','))].join('\n')
    const bom = '\ufeff'
    downloadBlob(bom + csvContent, `任务导出_${dayjs().format('YYYYMMDD')}.csv`, 'text/csv;charset=utf-8;')
    ElMessage.success('CSV已导出')
  } catch (error) {
    console.error('导出CSV失败', error)
  }
}

const exportExcel = async () => {
  try {
    const list = await fetchAllTasks(buildExportParams())
    const rows = buildExportRows(list)
    if (!rows.length) {
      ElMessage.warning('暂无可导出的任务')
      return
    }
    const worksheet = XLSX.utils.json_to_sheet(rows)
    const workbook = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(workbook, worksheet, '任务')
    XLSX.writeFile(workbook, `任务导出_${dayjs().format('YYYYMMDD')}.xlsx`)
    ElMessage.success('Excel已导出')
  } catch (error) {
    console.error('导出Excel失败', error)
  }
}

const exportWeeklyReport = async () => {
  try {
    const weekStart = dayjs().startOf('week')
    const weekEnd = dayjs().endOf('week')
    const params = buildExportParams({
      startDate: weekStart.format('YYYY-MM-DD'),
      endDate: weekEnd.format('YYYY-MM-DD'),
      scheduledDate: null
    })
    const list = await fetchAllTasks(params)
    if (!list.length) {
      ElMessage.warning('本周暂无任务数据')
      return
    }

    const totalTasks = list.length
    const completedTasks = list.filter(t => t.status === 'COMPLETED')
    const inProgressTasks = list.filter(t => t.status === 'IN_PROGRESS')
    const notStartedTasks = list.filter(t => t.status === 'NOT_STARTED')
    const overdueTasks = list.filter(t => isOverdue(t))

    const lines = [
      `# 学习周报 (${weekStart.format('YYYY-MM-DD')} ~ ${weekEnd.format('YYYY-MM-DD')})`,
      '',
      '## 本周概览',
      `- 总任务数：${totalTasks}`,
      `- 已完成：${completedTasks.length}`,
      `- 进行中：${inProgressTasks.length}`,
      `- 未开始：${notStartedTasks.length}`,
      `- 超期任务：${overdueTasks.length}`,
      '',
      '## 已完成任务',
      ...(completedTasks.length ? completedTasks.map(t => `- ${t.taskName}（${formatDate(t.scheduledDate)}）`) : ['- 无']),
      '',
      '## 进行中任务',
      ...(inProgressTasks.length ? inProgressTasks.map(t => `- ${t.taskName}（截止 ${formatDate(t.deadline)}）`) : ['- 无']),
      '',
      '## 未开始任务',
      ...(notStartedTasks.length ? notStartedTasks.map(t => `- ${t.taskName}（计划 ${formatDate(t.scheduledDate)}）`) : ['- 无']),
      '',
      '## 超期任务',
      ...(overdueTasks.length ? overdueTasks.map(t => `- ${t.taskName}（截止 ${formatDate(t.deadline)}）`) : ['- 无']),
      '',
      '## 下周计划',
      '- （可在此填写下周目标）'
    ]

    downloadBlob(lines.join('\n'), `学习周报_${weekStart.format('YYYYMMDD')}_${weekEnd.format('YYYYMMDD')}.md`, 'text/markdown;charset=utf-8;')
    ElMessage.success('周报模板已导出')
  } catch (error) {
    console.error('导出周报失败', error)
  }
}

onMounted(() => {
  fetchData()
  fetchOptions()
})
</script>

<style scoped>
/* ═══════════════════════════════════════════════════════════════
   Search Bar
   ═══════════════════════════════════════════════════════════════ */
.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  gap: 16px;
  flex-wrap: wrap;
}

.search-filters {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  flex: 1;
  min-width: 0;
}

.filter-select,
.filter-input {
  width: 140px;
}

.filter-date {
  width: 140px;
}

.search-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.btn-primary {
  display: flex;
  align-items: center;
  gap: 6px;
  background: var(--gradient-ink);
  color: var(--paper-white);
  border: none;
  border-radius: var(--radius-md);
  padding: 10px 18px;
  font-size: 13px;
  letter-spacing: 0.05em;
  cursor: pointer;
  transition: all var(--transition-base);
  box-shadow: var(--shadow-sm);

  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-md);
  }

  .el-icon {
    font-size: 14px;
  }
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.8);
  color: var(--ink-light);
  border: 1px solid rgba(26, 29, 35, 0.1);
  border-radius: var(--radius-md);
  padding: 10px 16px;
  font-size: 13px;
  letter-spacing: 0.05em;
  cursor: pointer;
  transition: all var(--transition-base);

  &:hover {
    background: rgba(255, 255, 255, 0.95);
    color: var(--ink-deep);
    border-color: var(--sage);
  }
}

.option-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 8px;
}

/* ═══════════════════════════════════════════════════════════════
   Empty State
   ═══════════════════════════════════════════════════════════════ */
.empty-state {
  text-align: center;
  padding: 80px 24px;
  color: var(--muted);
  position: relative;
  border: 2px dashed rgba(124, 139, 125, 0.2);
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.4), rgba(255, 255, 255, 0.2));

  &::before {
    content: '';
    position: absolute;
    width: 240px;
    height: 240px;
    background: radial-gradient(circle, rgba(124, 139, 125, 0.06), transparent 60%);
    top: -100px;
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
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  border-radius: 20px;
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

/* ═══════════════════════════════════════════════════════════════
   Task Grid
   ═══════════════════════════════════════════════════════════════ */
.task-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.task-card {
  background: rgba(255, 255, 255, 0.7);
  border-radius: var(--radius-lg);
  padding: 20px;
  border: 2px solid rgba(255, 255, 255, 0.8);
  border-left-width: 4px;
  border-left-style: solid;
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-base);
  animation: fadeInUp 0.5s var(--ease-smooth);
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-3px);
    box-shadow: var(--shadow-md);
  }

  &.completed {
    opacity: 0.6;
  }

  &.overdue {
    border-left-color: var(--cinnabar) !important;
  }
}

.task-header {
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

.task-type {
  font-size: 11px;
  color: var(--muted);
  letter-spacing: 0.05em;
}

.task-body {
  flex: 1;
  margin-bottom: 16px;
}

.task-name {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 600;
  color: var(--ink-deep);
  letter-spacing: 0.05em;
  margin: 0 0 8px;
}

.task-remark {
  font-size: 13px;
  color: var(--ink-light);
  line-height: 1.5;
  margin-bottom: 12px;
}

.task-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--muted);

  .el-icon {
    font-size: 13px;
  }

  &.meta-danger {
    color: var(--cinnabar);
  }
}

.task-priority {
  display: flex;
  align-items: center;
  gap: 8px;
}

.priority-label {
  font-size: 12px;
  color: var(--muted);
}

.priority-dots {
  display: flex;
  gap: 3px;
}

.priority-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: rgba(203, 213, 224, 0.3);
  transition: all var(--transition-base);

  &.active {
    background: var(--gold-subtle);
    box-shadow: 0 0 0 2px rgba(212, 168, 85, 0.2);
  }
}

.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid rgba(26, 29, 35, 0.06);
}

.task-status {
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

.task-actions {
  display: flex;
  gap: 8px;
}

.btn-action {
  padding: 6px 12px;
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

.btn-text {
  background: none;
  border: none;
  color: var(--ink-light);
  font-size: 12px;
  cursor: pointer;
  padding: 6px 0;
  letter-spacing: 0.05em;
  transition: all var(--transition-fast);

  &:hover {
    color: var(--ink-deep);
  }

  &.btn-danger {
    color: var(--cinnabar);

    &:hover {
      color: #d96b6b;
    }
  }
}

/* ═══════════════════════════════════════════════════════════════
   Pagination
   ═══════════════════════════════════════════════════════════════ */
.pagination {
  display: flex;
  justify-content: flex-end;
}

/* ═══════════════════════════════════════════════════════════════
   Dialog
   ═══════════════════════════════════════════════════════════════ */
.task-form,
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
  font-size: 14px;
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
</style>
