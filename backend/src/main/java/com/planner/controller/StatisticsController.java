package com.planner.controller;

import com.planner.common.Result;
import com.planner.service.StatisticsService;
import com.planner.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计Controller
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 获取仪表盘数据
     */
    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboard() {
        return Result.success(statisticsService.getDashboard());
    }

    /**
     * 获取连续学习天数
     */
    @GetMapping("/consecutive-days")
    public Result<Integer> getConsecutiveDays() {
        return Result.success(statisticsService.getConsecutiveDays());
    }

    /**
     * 获取全年每日统计
     */
    @GetMapping("/yearly")
    public Result<java.util.List<com.planner.vo.DailyStatVO>> getYearlyStats(
            @RequestParam(required = false) Integer year) {
        return Result.success(statisticsService.getYearlyStats(year));
    }
}
