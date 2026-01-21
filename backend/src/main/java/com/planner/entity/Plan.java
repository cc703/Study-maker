package com.planner.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学习方向实体
 */
@Data
public class Plan {

    private Long id;
    private Long userId;
    private String planName;
    private String description;
    private Integer priority;
    private String color;
    private Integer status;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
