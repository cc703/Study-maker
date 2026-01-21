package com.planner.service;

import com.planner.vo.DashboardVO;

/**
 * 统计Service接口
 */
public interface StatisticsService {

    /**
     * 获取仪表盘数据
     */
    DashboardVO getDashboard();

    /**
     * 计算连续学习天数
     */
    Integer getConsecutiveDays();

    /**
     * 获取全年每日统计
     */
    java.util.List<com.planner.vo.DailyStatVO> getYearlyStats(Integer year);
}
