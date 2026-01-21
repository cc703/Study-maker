package com.planner.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学习记录实体
 */
@Data
public class StudyRecord {

    private Long id;
    private Long userId;
    private LocalDate recordDate;
    private Integer totalTasks;
    private Integer completedTasks;
    private Integer totalTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
