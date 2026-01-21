import dayjs from 'dayjs'

/**
 * 格式化日期
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
    if (!date) return ''
    return dayjs(date).format(format)
}

/**
 * 格式化日期时间
 */
export function formatDateTime(date, format = 'YYYY-MM-DD HH:mm:ss') {
    if (!date) return ''
    return dayjs(date).format(format)
}

/**
 * 获取任务状态文本
 */
export function getStatusText(status) {
    const map = {
        'NOT_STARTED': '未开始',
        'IN_PROGRESS': '进行中',
        'COMPLETED': '已完成',
        'DELAYED': '延期'
    }
    return map[status] || status
}

/**
 * 获取任务状态类型
 */
export function getStatusType(status) {
    const map = {
        'NOT_STARTED': 'info',
        'IN_PROGRESS': 'primary',
        'COMPLETED': 'success',
        'DELAYED': 'danger'
    }
    return map[status] || 'info'
}

/**
 * 获取任务类型文本
 */
export function getTaskTypeText(type) {
    const map = {
        'STUDY': '学习',
        'PRACTICE': '练习',
        'REVIEW': '复盘',
        'MOCK': '模拟'
    }
    return map[type] || type
}

/**
 * 获取阶段状态文本
 */
export function getStageStatusText(status) {
    const map = {
        0: '未开始',
        1: '进行中',
        2: '已完成'
    }
    return map[status] || status
}

/**
 * 获取阶段状态类型
 */
export function getStageStatusType(status) {
    const map = {
        0: 'info',
        1: 'primary',
        2: 'success'
    }
    return map[status] || 'info'
}

/**
 * 格式化分钟为小时分钟
 */
export function formatMinutes(minutes) {
    if (!minutes) return '0分钟'
    if (minutes < 60) return `${minutes}分钟`
    const hours = Math.floor(minutes / 60)
    const mins = minutes % 60
    return mins > 0 ? `${hours}小时${mins}分钟` : `${hours}小时`
}
