package com.planner.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 任务查询条件DTO
 */
@Data
public class TaskQueryDTO {

    private Long planId;

    private Long stageId;

    private String taskType;

    private String status;

    private LocalDate scheduledDate;

    private LocalDate startDate;

    private LocalDate endDate;

    private String keyword;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
