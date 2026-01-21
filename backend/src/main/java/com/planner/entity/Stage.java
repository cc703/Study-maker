package com.planner.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 阶段目标实体
 */
@Data
public class Stage {

    private Long id;
    private Long planId;
    private String stageName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String target;
    private Integer status;
    private Integer progress;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联字段
    private String planName;
    private String planColor;
}
