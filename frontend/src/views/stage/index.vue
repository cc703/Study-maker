<template>
  <div class="stage-page">
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-group">
        <el-select
          v-model="filterPlanId"
          placeholder="筛选学习方向"
          clearable
          class="filter-select"
        >
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
      </div>
      <button class="btn-primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        <span>新增阶段</span>
      </button>
    </div>

    <!-- 阶段卡片列表 -->
    <div v-if="filteredStageList.length === 0" class="empty-state">
      <div class="empty-illustration">
        <el-icon><Medal /></el-icon>
      </div>
      <p>{{ filterPlanId ? '该方向暂无阶段' : '暂无阶段' }}</p>
      <p class="empty-hint">点击上方按钮创建新阶段</p>
    </div>

    <div v-else class="stage-list">
      <div
        v-for="(stage, index) in filteredStageList"
        :key="stage.id"
        class="stage-card"
        :style="{ animationDelay: `${index * 0.06}s`, borderColor: stage.planColor }"
      >
        <div class="stage-header">
          <div class="plan-badge" :style="{ background: `${stage.planColor}20`, color: stage.planColor }">
            {{ stage.planName }}
          </div>
          <div class="stage-status" :class="`status-${stage.status}`">
            {{ getStageStatusText(stage.status) }}
          </div>
        </div>

        <div class="stage-body">
          <h3 class="stage-name">{{ stage.stageName }}</h3>
          <p class="stage-description">{{ stage.description || '暂无描述' }}</p>

          <div class="stage-progress">
            <div class="progress-label">
              <span>进度</span>
              <span class="progress-value">{{ stage.progress || 0 }}%</span>
            </div>
            <div class="progress-track">
              <div
                class="progress-bar"
                :style="{ width: `${stage.progress || 0}%`, background: stage.planColor }"
              ></div>
            </div>
          </div>

          <div class="stage-meta">
            <div class="meta-item">
              <el-icon><Calendar /></el-icon>
              <span>{{ formatDate(stage.startDate) }} - {{ formatDate(stage.endDate) }}</span>
            </div>
          </div>

          <div v-if="stage.target" class="stage-target">
            <el-icon><Aim /></el-icon>
            <span>{{ stage.target }}</span>
          </div>
        </div>

        <div class="stage-actions">
          <button class="btn-text" @click="handleEdit(stage)">编辑</button>
          <button class="btn-text btn-danger" @click="handleDelete(stage)">删除</button>
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑阶段' : '新增阶段'"
      width="550px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="stage-form"
      >
        <el-form-item label="所属方向" prop="planId">
          <el-select v-model="form.planId" placeholder="请选择学习方向" style="width: 100%;" @change="handlePlanChange">
            <el-option
              v-for="plan in planList"
              :key="plan.id"
              :label="plan.planName"
              :value="plan.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="阶段名称" prop="stageName">
          <el-input v-model="form.stageName" placeholder="请输入阶段名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="2"
            placeholder="请输入描述"
          />
        </el-form-item>
        <el-form-item label="时间范围" prop="dateRange">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="阶段目标" prop="target">
          <el-input
            v-model="form.target"
            type="textarea"
            :rows="2"
            placeholder="请输入阶段目标"
          />
        </el-form-item>
        <el-form-item label="进度" prop="progress">
          <el-slider v-model="form.progress" :max="100" :step="5" show-input />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">未开始</el-radio>
            <el-radio :value="1">进行中</el-radio>
            <el-radio :value="2">已完成</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="dialogVisible = false">取消</button>
          <button class="btn-confirm" @click="handleSubmit">确定</button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStageList, createStage, updateStage, deleteStage } from '@/api/stage'
import { getPlanList } from '@/api/plan'
import { formatDate, getStageStatusText } from '@/utils'

const stageList = ref([])
const planList = ref([])
const filterPlanId = ref(null)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const dateRange = ref([])

const filteredStageList = computed(() => {
  if (!filterPlanId.value) return stageList.value
  return stageList.value.filter(s => s.planId === filterPlanId.value)
})

const defaultForm = {
  id: null,
  planId: null,
  stageName: '',
  description: '',
  startDate: null,
  endDate: null,
  target: '',
  status: 0,
  progress: 0,
  sortOrder: 0
}

const form = ref({ ...defaultForm })

const rules = {
  planId: [{ required: true, message: '请选择学习方向', trigger: 'change' }],
  stageName: [
    { required: true, message: '请输入阶段名称', trigger: 'blur' },
    { max: 100, message: '名称不能超过100个字符', trigger: 'blur' }
  ]
}

watch(dateRange, (val) => {
  if (val && val.length === 2) {
    form.value.startDate = val[0]
    form.value.endDate = val[1]
  } else {
    form.value.startDate = null
    form.value.endDate = null
  }
})

const fetchData = async () => {
  try {
    const [stageRes, planRes] = await Promise.all([
      getStageList(),
      getPlanList()
    ])
    stageList.value = stageRes.data || []
    planList.value = planRes.data || []
  } catch (error) {
    console.error('获取数据失败', error)
  }
}

const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  if (row.startDate && row.endDate) {
    dateRange.value = [row.startDate, row.endDate]
  }
  dialogVisible.value = true
}

const handlePlanChange = () => {
  form.value.stageId = null
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    '删除阶段将同时删除其下的任务，确定要删除吗？',
    '确认删除',
    { type: 'warning', confirmButtonText: '确定删除', cancelButtonText: '取消' }
  ).then(async () => {
    try {
      await deleteStage(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error('删除失败', error)
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await updateStage(form.value)
    } else {
      await createStage(form.value)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('提交失败', error)
  }
}

const resetForm = () => {
  form.value = { ...defaultForm }
  dateRange.value = []
  formRef.value?.resetFields()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
/* ═══════════════════════════════════════════════════════════════
   Filter Bar
   ═══════════════════════════════════════════════════════════════ */
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  gap: 16px;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-select {
  width: 220px;

  :deep(.el-input__wrapper) {
    border-radius: var(--radius-md);
    background: rgba(255, 255, 255, 0.8);
    box-shadow: var(--shadow-sm);
  }

  .option-dot {
    display: inline-block;
    width: 10px;
    height: 10px;
    border-radius: 50%;
    margin-right: 8px;
  }
}

.btn-primary {
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--gradient-ink);
  color: var(--paper-white);
  border: none;
  border-radius: var(--radius-md);
  padding: 12px 24px;
  font-size: 14px;
  letter-spacing: 0.05em;
  cursor: pointer;
  transition: all var(--transition-base);
  box-shadow: var(--shadow-md);
  white-space: nowrap;

  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-lg);
  }

  .el-icon {
    font-size: 16px;
  }
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
   Stage List
   ═══════════════════════════════════════════════════════════════ */
.stage-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 20px;
}

.stage-card {
  background: rgba(255, 255, 255, 0.7);
  border-radius: var(--radius-xl);
  padding: 24px;
  border: 2px solid rgba(255, 255, 255, 0.8);
  box-shadow: var(--shadow-md);
  transition: all var(--transition-base);
  animation: fadeInUp 0.5s var(--ease-smooth);
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-float);
  }
}

.stage-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(26, 29, 35, 0.06);
}

.plan-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: var(--radius-pill);
  font-size: 12px;
  letter-spacing: 0.05em;
  font-weight: 500;
}

.stage-status {
  font-size: 12px;
  padding: 6px 12px;
  border-radius: var(--radius-pill);
  letter-spacing: 0.05em;
  font-weight: 500;

  &.status-0 {
    background: rgba(154, 163, 168, 0.15);
    color: var(--muted);
  }

  &.status-1 {
    background: rgba(61, 90, 128, 0.15);
    color: var(--ink-light);
  }

  &.status-2 {
    background: rgba(124, 139, 125, 0.2);
    color: var(--sage);
  }
}

.stage-body {
  flex: 1;
  margin-bottom: 16px;
}

.stage-name {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 600;
  color: var(--ink-deep);
  letter-spacing: 0.05em;
  margin: 0 0 12px;
}

.stage-description {
  font-size: 14px;
  color: var(--ink-light);
  line-height: 1.6;
  margin-bottom: 20px;
  letter-spacing: 0.02em;
}

.stage-progress {
  margin-bottom: 16px;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 13px;
  color: var(--muted);
}

.progress-value {
  font-family: var(--font-mono);
  font-weight: 600;
  color: var(--ink-deep);
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

.stage-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--muted);

  .el-icon {
    font-size: 14px;
    color: var(--sage);
  }
}

.stage-target {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px;
  background: rgba(26, 29, 35, 0.03);
  border-radius: var(--radius-md);
  font-size: 13px;
  color: var(--ink-light);
  line-height: 1.5;

  .el-icon {
    font-size: 16px;
    color: var(--gold-subtle);
    flex-shrink: 0;
    margin-top: 1px;
  }
}

.stage-actions {
  display: flex;
  gap: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(26, 29, 35, 0.06);
}

.btn-text {
  background: none;
  border: none;
  color: var(--ink-light);
  font-size: 13px;
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
   Dialog
   ═══════════════════════════════════════════════════════════════ */
.stage-form {
  padding: 12px 0;
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
