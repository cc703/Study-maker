package com.planner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 任务状态更新DTO
 */
@Data
public class TaskStatusDTO {

    @NotNull(message = "任务ID不能为空")
    private Long id;

    @NotBlank(message = "状态不能为空")
    private String status;

    private Integer actualTime;

    private String remark;
}
