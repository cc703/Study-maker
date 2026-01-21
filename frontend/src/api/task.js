import request from '@/utils/request'

/**
 * 分页查询任务
 */
export function getTaskList(params) {
    return request.get('/tasks', { params })
}

/**
 * 获取今日任务
 */
export function getTodayTasks() {
    return request.get('/tasks/today')
}

/**
 * 获取指定日期任务
 */
export function getTasksByDate(date) {
    return request.get(`/tasks/date/${date}`)
}

/**
 * 获取截止日期任务
 */
export function getTasksByDeadline(date) {
    return request.get(`/tasks/deadline/${date}`)
}

/**
 * 获取超期任务
 */
export function getOverdueTasks() {
    return request.get('/tasks/overdue')
}

/**
 * 获取任务详情
 */
export function getTaskById(id) {
    return request.get(`/tasks/${id}`)
}

/**
 * 新增任务
 */
export function createTask(data) {
    return request.post('/tasks', data)
}

/**
 * 更新任务
 */
export function updateTask(data) {
    return request.put('/tasks', data)
}

/**
 * 删除任务
 */
export function deleteTask(id) {
    return request.delete(`/tasks/${id}`)
}

/**
 * 更新任务状态
 */
export function updateTaskStatus(data) {
    return request.put('/tasks/status', data)
}

/**
 * 开始任务
 */
export function startTask(id) {
    return request.put(`/tasks/${id}/start`)
}

/**
 * 完成任务
 */
export function completeTask(id, actualTime) {
    return request.put(`/tasks/${id}/complete`, null, { params: { actualTime } })
}
