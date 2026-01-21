import request from '@/utils/request'

/**
 * 获取仪表盘数据
 */
export function getDashboard() {
    return request.get('/statistics/dashboard')
}

/**
 * 获取连续学习天数
 */
export function getConsecutiveDays() {
    return request.get('/statistics/consecutive-days')
}

/**
 * 获取全年每日统计
 */
export function getYearlyStats(year) {
    return request.get('/statistics/yearly', { params: { year } })
}
