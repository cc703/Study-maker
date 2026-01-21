package com.planner.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 备忘录实体
 */
@Data
public class Memo {

    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String color;
    private Integer pinned;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
