import request from '@/utils/request'

/**
 * 获取备忘录列表
 */
export function getMemoList(params) {
    return request.get('/memos', { params })
}

/**
 * 获取备忘录详情
 */
export function getMemoById(id) {
    return request.get(`/memos/${id}`)
}

/**
 * 新增备忘录
 */
export function createMemo(data) {
    return request.post('/memos', data)
}

/**
 * 更新备忘录
 */
export function updateMemo(data) {
    return request.put('/memos', data)
}

/**
 * 删除备忘录
 */
export function deleteMemo(id) {
    return request.delete(`/memos/${id}`)
}
