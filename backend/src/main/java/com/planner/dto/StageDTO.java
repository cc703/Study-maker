package com.planner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

/**
 * 阶段DTO
 */
@Data
public class StageDTO {

    private Long id;

    @NotNull(message = "所属学习方向不能为空")
    private Long planId;

    @NotBlank(message = "阶段名称不能为空")
    @Size(max = 100, message = "阶段名称不能超过100个字符")
    private String stageName;

    @Size(max = 500, message = "描述不能超过500个字符")
    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    @Size(max = 500, message = "目标不能超过500个字符")
    private String target;

    private Integer status = 0;

    private Integer progress = 0;

    private Integer sortOrder = 0;
}
