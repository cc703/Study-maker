package com.planner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 学习方向DTO
 */
@Data
public class PlanDTO {

    private Long id;

    @NotBlank(message = "学习方向名称不能为空")
    @Size(max = 100, message = "学习方向名称不能超过100个字符")
    private String planName;

    @Size(max = 500, message = "描述不能超过500个字符")
    private String description;

    private Integer priority = 3;

    private String color = "#409EFF";

    private Integer status = 1;

    private Integer sortOrder = 0;
}
