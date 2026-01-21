<template>
  <div class="memo-page">
    <!-- 操作栏 -->
    <div class="toolbar">
      <div class="search-input-group">
        <el-icon><Search /></el-icon>
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索备忘录"
          clearable
          @keyup.enter="fetchData"
          class="search-input"
        />
      </div>
      <div class="toolbar-actions">
        <button class="btn-secondary" @click="resetQuery">重置</button>
        <button class="btn-primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          <span>新建备忘录</span>
        </button>
      </div>
    </div>

    <!-- 备忘录网格 -->
    <div v-if="!memoList.length" class="empty-state">
      <div class="empty-illustration">
        <el-icon><Document /></el-icon>
      </div>
      <p>暂无备忘录</p>
      <p class="empty-hint">点击上方按钮创建第一条备忘录</p>
    </div>

    <div v-else class="memo-grid">
      <div
        v-for="(memo, index) in memoList"
        :key="memo.id"
        class="memo-card"
        :class="{ pinned: memo.pinned === 1 }"
        :style="{
          animationDelay: `${index * 0.06}s`,
          borderLeftColor: memo.color || '#7c8b7d'
        }"
      >
        <div class="memo-header">
          <span class="memo-title">{{ memo.title }}</span>
          <div
            v-if="memo.pinned === 1"
            class="pin-badge"
            :style="{ background: `${memo.color}30`, color: memo.color || '#7c8b7d' }"
          >
            <el-icon><Top /></el-icon>
            <span>置顶</span>
          </div>
        </div>

        <div class="memo-content">
          {{ memo.content || '（无内容）' }}
        </div>

        <div class="memo-actions">
          <button
            class="btn-text"
            :class="{ active: memo.pinned === 1 }"
            @click="togglePinned(memo)"
          >
            <el-icon v-if="memo.pinned === 1"><Bottom /></el-icon>
            <el-icon v-else><Top /></el-icon>
          </button>
          <button class="btn-text" @click="handleEdit(memo)">编辑</button>
          <button class="btn-text btn-danger" @click="handleDelete(memo)">删除</button>
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑备忘录' : '新建备忘录'"
      width="560px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="90px"
        class="memo-form"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="6"
            placeholder="请输入内容"
          />
        </el-form-item>
        <el-form-item label="颜色" prop="color">
          <div class="color-picker-group">
            <el-color-picker v-model="form.color" />
            <span class="color-hint">选择一个颜色来标识此备忘录</span>
          </div>
        </el-form-item>
        <el-form-item label="置顶" prop="pinned">
          <el-switch
            v-model="form.pinned"
            :active-value="1"
            :inactive-value="0"
            active-text="置顶"
            inactive-text="不置顶"
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
import { getMemoList, createMemo, updateMemo, deleteMemo } from '@/api/memo'

const memoList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const queryParams = ref({
  keyword: ''
})

const defaultForm = {
  id: null,
  title: '',
  content: '',
  color: '#7c8b7d',
  pinned: 0
}

const form = ref({ ...defaultForm })

const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { max: 100, message: '标题不能超过100个字符', trigger: 'blur' }
  ],
  content: [
    { max: 4000, message: '内容不能超过4000个字符', trigger: 'blur' }
  ]
}

const fetchData = async () => {
  try {
    const res = await getMemoList(queryParams.value)
    memoList.value = res.data || []
  } catch (error) {
    console.error('获取备忘录失败', error)
  }
}

const resetQuery = () => {
  queryParams.value = { keyword: '' }
  fetchData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (memo) => {
  isEdit.value = true
  form.value = { ...memo }
  dialogVisible.value = true
}

const togglePinned = async (memo) => {
  try {
    await updateMemo({
      id: memo.id,
      title: memo.title,
      content: memo.content,
      color: memo.color,
      pinned: memo.pinned === 1 ? 0 : 1
    })
    ElMessage.success('操作成功')
    fetchData()
  } catch (error) {
    console.error('更新置顶失败', error)
  }
}

const handleDelete = (memo) => {
  ElMessageBox.confirm('确定要删除该备忘录吗？', '确认删除', { type: 'warning' })
    .then(async () => {
      try {
        await deleteMemo(memo.id)
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
      await updateMemo(form.value)
    } else {
      await createMemo(form.value)
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
/* ═════════════════════════════════════════════════════════════
   Toolbar
   ═════════════════════════════════════════════════════════════ */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  gap: 16px;
}

.search-input-group {
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: var(--radius-md);
  padding: 8px 16px;
  box-shadow: var(--shadow-sm);
  flex: 1;
  max-width: 320px;

  .el-icon {
    font-size: 16px;
    color: var(--muted);
  }

  .search-input {
    flex: 1;
  }
}

.toolbar-actions {
  display: flex;
  gap: 12px;
}

.btn-primary {
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--gradient-ink);
  color: var(--paper-white);
  border: none;
  border-radius: var(--radius-md);
  padding: 10px 20px;
  font-size: 13px;
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

.btn-secondary {
  background: rgba(255, 255, 255, 0.8);
  color: var(--ink-light);
  border: 1px solid rgba(26, 29, 35, 0.1);
  border-radius: var(--radius-md);
  padding: 10px 18px;
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

/* ═════════════════════════════════════════════════════════════
   Empty State
   ═════════════════════════════════════════════════════════════ */
.empty-state {
  text-align: center;
  padding: 80px 24px;
  color: var(--muted);
  position: relative;
  border: 2px dashed rgba(124, 139, 125, 0.2);
  border-radius: var(--radius-xl);
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

/* ═════════════════════════════════════════════════════════════
   Memo Grid
   ═════════════════════════════════════════════════════════════ */
.memo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.memo-card {
  background: rgba(255, 255, 255, 0.7);
  border-radius: var(--radius-xl);
  padding: 20px;
  border: 2px solid rgba(255, 255, 255, 0.8);
  border-left-width: 4px;
  border-left-style: solid;
  box-shadow: var(--shadow-md);
  transition: all var(--transition-base);
  animation: fadeInUp 0.5s var(--ease-smooth);
  display: flex;
  flex-direction: column;
  min-height: 200px;

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-float);
  }

  &.pinned {
    background: rgba(255, 255, 255, 0.85);
    box-shadow: var(--shadow-lg);
  }
}

.memo-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(26, 29, 35, 0.06);
}

.memo-title {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 600;
  color: var(--ink-deep);
  letter-spacing: 0.05em;
  line-height: 1.3;
}

.pin-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: var(--radius-pill);
  font-size: 11px;
  letter-spacing: 0.05em;
  font-weight: 500;

  .el-icon {
    font-size: 12px;
  }
}

.memo-content {
  font-size: 14px;
  color: var(--ink-light);
  line-height: 1.6;
  flex: 1;
  margin-bottom: 16px;
  white-space: pre-wrap;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 6;
  -webkit-box-orient: vertical;
}

.memo-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding-top: 12px;
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
  display: flex;
  align-items: center;
  gap: 4px;

  .el-icon {
    font-size: 14px;
  }

  &:hover {
    color: var(--ink-deep);
  }

  &.active {
    color: var(--gold-subtle);

    &:hover {
      color: var(--gold-subtle);
    }
  }

  &.btn-danger {
    color: var(--cinnabar);

    &:hover {
      color: #d96b6b;
    }
  }
}

/* ═════════════════════════════════════════════════════════════
   Dialog
   ═════════════════════════════════════════════════════════════ */
.memo-form {
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
</style>
