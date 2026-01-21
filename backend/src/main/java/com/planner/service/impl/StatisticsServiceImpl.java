package com.planner.service.impl;

import com.planner.mapper.PlanMapper;
import com.planner.mapper.TaskMapper;
import com.planner.service.StatisticsService;
import com.planner.vo.DailyStatVO;
import com.planner.vo.DashboardVO;
import com.planner.vo.PlanStatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 统计Service实现
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final TaskMapper taskMapper;
    private final PlanMapper planMapper;

    private static final Long DEFAULT_USER_ID = 1L;

    @Override
    public DashboardVO getDashboard() {
        DashboardVO dashboard = new DashboardVO();
        LocalDate today = LocalDate.now();

        // 今日任务统计
        int todayTotal = taskMapper.countByScheduledDate(today);
        int todayCompleted = taskMapper.countByScheduledDateAndStatus(today, "COMPLETED");
        int todayInProgress = taskMapper.countByScheduledDateAndStatus(today, "IN_PROGRESS");
        int todayNotStarted = taskMapper.countByScheduledDateAndStatus(today, "NOT_STARTED");

        dashboard.setTodayTotal(todayTotal);
        dashboard.setTodayCompleted(todayCompleted);
        dashboard.setTodayInProgress(todayInProgress);
        dashboard.setTodayNotStarted(todayNotStarted);

        // 总体统计
        dashboard.setTotalPlans(planMapper.countByUserId(DEFAULT_USER_ID));
        dashboard.setTotalTasks(taskMapper.countAll());
        dashboard.setTotalCompletedTasks(taskMapper.countCompleted());

        // 连续学习天数
        dashboard.setConsecutiveDays(getConsecutiveDays());

        // 各学习方向统计
        List<PlanStatVO> planStats = taskMapper.getPlanStats();
        dashboard.setPlanStats(planStats);

        // 本周趋势
        LocalDate weekStart = today.minusDays(6);
        List<DailyStatVO> weeklyTrend = taskMapper.getWeeklyStats(weekStart, today);
        dashboard.setWeeklyTrend(weeklyTrend);

        return dashboard;
    }

    @Override
    public Integer getConsecutiveDays() {
        LocalDate today = LocalDate.now();
        int consecutiveDays = 0;

        for (int i = 0; i < 365; i++) {
            LocalDate checkDate = today.minusDays(i);
            int completed = taskMapper.countByScheduledDateAndStatus(checkDate, "COMPLETED");
            if (completed > 0) {
                consecutiveDays++;
            } else if (i > 0) {
                break;
            }
        }

        return consecutiveDays;
    }

    @Override
    public List<DailyStatVO> getYearlyStats(Integer year) {
        int targetYear = (year == null || year <= 0) ? LocalDate.now().getYear() : year;
        LocalDate startDate = LocalDate.of(targetYear, 1, 1);
        LocalDate endDate = LocalDate.of(targetYear, 12, 31);
        return taskMapper.getDailyStats(startDate, endDate);
    }
}
