package com.planner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

/**
 * 任务DTO
 */
@Data
public class TaskDTO {

    private Long id;

    @NotBlank(message = "任务名称不能为空")
    @Size(max = 200, message = "任务名称不能超过200个字符")
    private String taskName;

    @NotNull(message = "所属学习方向不能为空")
    private Long planId;

    private Long stageId;

    private String taskType = "STUDY";

    private Integer priority = 3;

    private Integer estimatedTime;

    private LocalDate deadline;

    private String status = "NOT_STARTED";

    private Integer actualTime;

    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;

    private LocalDate scheduledDate;
}
