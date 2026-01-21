package com.planner.vo;

import lombok.Data;

/**
 * 学习方向统计VO
 */
@Data
public class PlanStatVO {

    private Long planId;
    private String planName;
    private String color;
    private Integer totalTasks;
    private Integer completedTasks;
    private Double completionRate;
    private Integer totalTime;
}
