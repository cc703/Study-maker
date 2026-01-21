import request from '@/utils/request'

/**
 * 获取所有学习方向
 */
export function getPlanList() {
    return request.get('/plans')
}

/**
 * 获取启用的学习方向
 */
export function getActivePlanList() {
    return request.get('/plans/active')
}

/**
 * 获取学习方向详情
 */
export function getPlanById(id) {
    return request.get(`/plans/${id}`)
}

/**
 * 新增学习方向
 */
export function createPlan(data) {
    return request.post('/plans', data)
}

/**
 * 更新学习方向
 */
export function updatePlan(data) {
    return request.put('/plans', data)
}

/**
 * 删除学习方向
 */
export function deletePlan(id) {
    return request.delete(`/plans/${id}`)
}

/**
 * 切换学习方向状态
 */
export function togglePlanStatus(id) {
    return request.put(`/plans/${id}/toggle`)
}
