package com.planner.vo;

import lombok.Data;
import java.util.List;

/**
 * 仪表盘统计VO
 */
@Data
public class DashboardVO {

    // 今日任务统计
    private Integer todayTotal;
    private Integer todayCompleted;
    private Integer todayInProgress;
    private Integer todayNotStarted;

    // 总体统计
    private Integer totalPlans;
    private Integer totalTasks;
    private Integer totalCompletedTasks;

    // 连续学习天数
    private Integer consecutiveDays;

    // 各学习方向完成率
    private List<PlanStatVO> planStats;

    // 本周趋势
    private List<DailyStatVO> weeklyTrend;
}
