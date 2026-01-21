package com.planner.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 任务状态流转记录实体
 */
@Data
public class TaskLog {

    private Long id;
    private Long taskId;
    private String fromStatus;
    private String toStatus;
    private String remark;
    private LocalDateTime operationTime;
}
