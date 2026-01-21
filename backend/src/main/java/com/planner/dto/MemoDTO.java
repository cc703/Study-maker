package com.planner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 备忘录DTO
 */
@Data
public class MemoDTO {

    private Long id;

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题不能超过100个字符")
    private String title;

    @Size(max = 4000, message = "内容不能超过4000个字符")
    private String content;

    @Size(max = 20, message = "颜色不能超过20个字符")
    private String color;

    @NotNull(message = "置顶状态不能为空")
    private Integer pinned;
}
