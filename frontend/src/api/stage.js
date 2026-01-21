import request from '@/utils/request'

/**
 * 获取所有阶段
 */
export function getStageList() {
    return request.get('/stages')
}

/**
 * 根据学习方向获取阶段
 */
export function getStagesByPlanId(planId) {
    return request.get(`/stages/plan/${planId}`)
}

/**
 * 获取阶段详情
 */
export function getStageById(id) {
    return request.get(`/stages/${id}`)
}

/**
 * 新增阶段
 */
export function createStage(data) {
    return request.post('/stages', data)
}

/**
 * 更新阶段
 */
export function updateStage(data) {
    return request.put('/stages', data)
}

/**
 * 删除阶段
 */
export function deleteStage(id) {
    return request.delete(`/stages/${id}`)
}

/**
 * 更新阶段进度
 */
export function updateStageProgress(id, progress) {
    return request.put(`/stages/${id}/progress`, null, { params: { progress } })
}
