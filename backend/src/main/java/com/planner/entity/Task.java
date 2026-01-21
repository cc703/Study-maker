package com.planner.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 任务实体
 */
@Data
public class Task {

    private Long id;
    private String taskName;
    private Long planId;
    private Long stageId;
    private String taskType;
    private Integer priority;
    private Integer estimatedTime;
    private LocalDate deadline;
    private String status;
    private Integer actualTime;
    private String remark;
    private LocalDate scheduledDate;
    private LocalDateTime completedTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联字段
    private String planName;
    private String planColor;
    private String stageName;
}
