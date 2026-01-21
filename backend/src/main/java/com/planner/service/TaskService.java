package com.planner.service;

import com.planner.common.PageResult;
import com.planner.dto.TaskDTO;
import com.planner.dto.TaskQueryDTO;
import com.planner.dto.TaskStatusDTO;
import com.planner.entity.Task;
import java.time.LocalDate;
import java.util.List;

/**
 * 任务Service接口
 */
public interface TaskService {

    /**
     * 分页查询任务
     */
    PageResult<Task> findByCondition(TaskQueryDTO query);

    /**
     * 根据ID查询
     */
    Task findById(Long id);

    /**
     * 获取今日任务
     */
    List<Task> findTodayTasks();

    /**
     * 获取指定日期任务
     */
    List<Task> findByScheduledDate(LocalDate date);

    /**
     * 获取截止日期任务
     */
    List<Task> findByDeadline(LocalDate date);

    /**
     * 获取超期任务
     */
    List<Task> findOverdueTasks();

    /**
     * 新增任务
     */
    Long create(TaskDTO dto);

    /**
     * 更新任务
     */
    void update(TaskDTO dto);

    /**
     * 删除任务
     */
    void delete(Long id);

    /**
     * 更新任务状态
     */
    void updateStatus(TaskStatusDTO dto);

    /**
     * 开始任务
     */
    void startTask(Long id);

    /**
     * 完成任务
     */
    void completeTask(Long id, Integer actualTime);
}
