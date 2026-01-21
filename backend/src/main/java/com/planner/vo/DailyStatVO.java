package com.planner.vo;

import lombok.Data;
import java.time.LocalDate;

/**
 * 每日统计VO
 */
@Data
public class DailyStatVO {

    private LocalDate date;
    private Integer totalTasks;
    private Integer completedTasks;
    private Integer totalTime;
}
