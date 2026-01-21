<template>
  <div class="plan-page">
    <!-- 操作栏 -->
    <div class="toolbar">
      <button class="btn-primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        <span>新增学习方向</span>
      </button>
    </div>

    <!-- 学习方向卡片网格 -->
    <div v-if="planList.length === 0" class="empty-state">
      <div class="empty-illustration">
        <el-icon><Flag /></el-icon>
      </div>
      <p>暂无学习方向</p>
      <p class="empty-hint">点击上方按钮创建第一个学习方向</p>
    </div>

    <div v-else class="plan-grid">
      <div
        v-for="(plan, index) in planList"
        :key="plan.id"
        class="plan-card"
        :class="{ disabled: plan.status === 0 }"
        :style="{ animationDelay: `${index * 0.08}s`, borderLeftColor: plan.color }"
      >
        <div class="plan-header">
          <div class="plan-info">
            <div class="color-indicator" :style="{ backgroundColor: plan.color }"></div>
            <h3 class="plan-name">{{ plan.planName }}</h3>
          </div>
          <div class="plan-status" :class="{ active: plan.status === 1 }">
            {{ plan.status === 1 ? '启用' : '停用' }}
          </div>
        </div>

        <div class="plan-body">
          <p class="plan-description">{{ plan.description || '暂无描述' }}</p>

          <div class="plan-meta">
            <div class="meta-item">
              <el-icon><Star /></el-icon>
              <span>优先级</span>
              <div class="priority-dots">
                <span
                  v-for="i in 5"
                  :key="i"
                  class="priority-dot"
                  :class="{ active: i <= plan.priority }"
                ></span>
              </div>
            </div>
          </div>
        </div>

        <div class="plan-actions">
          <button class="btn-text" @click="handleEdit(plan)">编辑</button>
          <button
            class="btn-text"
            :class="plan.status === 1 ? 'btn-warning' : 'btn-success'"
            @click="handleToggle(plan)"
          >
            {{ plan.status === 1 ? '停用' : '启用' }}
          </button>
          <button class="btn-text btn-danger" @click="handleDelete(plan)">删除</button>
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑学习方向' : '新增学习方向'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="plan-form"
      >
        <el-form-item label="名称" prop="planName">
          <el-input v-model="form.planName" placeholder="请输入学习方向名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-rate v-model="form.priority" :max="5" />
        </el-form-item>
        <el-form-item label="标识颜色" prop="color">
          <div class="color-picker-group">
            <el-color-picker v-model="form.color" />
            <span class="color-hint">选择一个颜色来标识此学习方向</span>
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="停用"
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPlanList, createPlan, updatePlan, deletePlan, togglePlanStatus } from '@/api/plan'

const planList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const defaultForm = {
  id: null,
  planName: '',
  description: '',
  priority: 3,
  color: '#7c8b7d',
  status: 1,
  sortOrder: 0
}

const form = ref({ ...defaultForm })

const rules = {
  planName: [
    { required: true, message: '请输入学习方向名称', trigger: 'blur' },
    { max: 100, message: '名称不能超过100个字符', trigger: 'blur' }
  ]
}

const fetchData = async () => {
  try {
    const res = await getPlanList()
    planList.value = res.data || []
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
  dialogVisible.value = true
}

const handleToggle = async (row) => {
  try {
    await togglePlanStatus(row.id)
    ElMessage.success('操作成功')
    fetchData()
  } catch (error) {
    console.error('操作失败', error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    '删除学习方向将同时删除其下的阶段和任务，确定要删除吗？',
    '确认删除',
    { type: 'warning', confirmButtonText: '确定删除', cancelButtonText: '取消' }
  ).then(async () => {
    try {
      await deletePlan(row.id)
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
      await updatePlan(form.value)
    } else {
      await createPlan(form.value)
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
  formRef.value?.resetFields()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
/* ═══════════════════════════════════════════════════════════════
   Toolbar
   ═══════════════════════════════════════════════════════════════ */
.toolbar {
  margin-bottom: 24px;
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
   Plan Grid
   ═══════════════════════════════════════════════════════════════ */
.plan-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.plan-card {
  background: rgba(255, 255, 255, 0.7);
  border-radius: var(--radius-xl);
  padding: 24px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-left-width: 5px;
  border-left-style: solid;
  box-shadow: var(--shadow-md);
  transition: all var(--transition-base);
  animation: fadeInUp 0.5s var(--ease-smooth);
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-float);
  }

  &.disabled {
    opacity: 0.6;
    filter: grayscale(0.3);
  }
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(26, 29, 35, 0.06);
}

.plan-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.color-indicator {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  flex-shrink: 0;
  box-shadow: 0 0 0 3px rgba(0, 0, 0, 0.05);
}

.plan-name {
  font-family: var(--font-display);
  font-size: 17px;
  font-weight: 600;
  color: var(--ink-deep);
  letter-spacing: 0.05em;
  margin: 0;
}

.plan-status {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: var(--radius-pill);
  letter-spacing: 0.05em;
  font-weight: 500;
  background: rgba(154, 163, 168, 0.15);
  color: var(--muted);

  &.active {
    background: rgba(124, 139, 125, 0.2);
    color: var(--sage);
  }
}

.plan-body {
  flex: 1;
  margin-bottom: 16px;
}

.plan-description {
  font-size: 14px;
  color: var(--ink-light);
  line-height: 1.6;
  margin-bottom: 16px;
  letter-spacing: 0.02em;
}

.plan-meta {
  display: flex;
  gap: 20px;
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

.priority-dots {
  display: flex;
  gap: 4px;
}

.priority-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(203, 213, 224, 0.3);
  transition: all var(--transition-base);

  &.active {
    background: var(--gold-subtle);
    box-shadow: 0 0 0 2px rgba(212, 168, 85, 0.2);
  }
}

.plan-actions {
  display: flex;
  gap: 12px;
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

  &.btn-warning {
    color: var(--cinnabar);

    &:hover {
      color: #d96b6b;
    }
  }

  &.btn-success {
    color: var(--sage);

    &:hover {
      color: #8fa694;
    }
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
.plan-form {
  padding: 12px 0;
}

.color-picker-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.color-hint {
  font-size: 12px;
  color: var(--muted);
  letter-spacing: 0.05em;
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
