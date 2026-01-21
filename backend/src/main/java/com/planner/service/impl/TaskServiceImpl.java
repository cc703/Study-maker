package com.planner.service.impl;

import com.planner.common.BusinessException;
import com.planner.common.PageResult;
import com.planner.dto.TaskDTO;
import com.planner.dto.TaskQueryDTO;
import com.planner.dto.TaskStatusDTO;
import com.planner.entity.Task;
import com.planner.entity.TaskLog;
import com.planner.mapper.TaskLogMapper;
import com.planner.mapper.TaskMapper;
import com.planner.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务Service实现
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;
    private final TaskLogMapper taskLogMapper;

    @Override
    public PageResult<Task> findByCondition(TaskQueryDTO query) {
        // 计算偏移量
        int offset = (query.getPageNum() - 1) * query.getPageSize();
        query.setPageNum(offset);

        List<Task> list = taskMapper.findByCondition(query);
        Long total = taskMapper.countByCondition(query);

        return PageResult.of(list, total, query.getPageNum(), query.getPageSize());
    }

    @Override
    public Task findById(Long id) {
        Task task = taskMapper.findById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        return task;
    }

    @Override
    public List<Task> findTodayTasks() {
        return taskMapper.findByScheduledDate(LocalDate.now());
    }

    @Override
    public List<Task> findByScheduledDate(LocalDate date) {
        return taskMapper.findByScheduledDate(date);
    }

    @Override
    public List<Task> findByDeadline(LocalDate date) {
        return taskMapper.findByDeadline(date);
    }

    @Override
    public List<Task> findOverdueTasks() {
        return taskMapper.findOverdueTasks(LocalDate.now());
    }

    @Override
    @Transactional
    public Long create(TaskDTO dto) {
        Task task = new Task();
        copyDtoToEntity(dto, task);
        taskMapper.insert(task);

        // 记录日志
        recordLog(task.getId(), null, task.getStatus(), "创建任务");

        return task.getId();
    }

    @Override
    @Transactional
    public void update(TaskDTO dto) {
        Task task = findById(dto.getId());
        String oldStatus = task.getStatus();
        copyDtoToEntity(dto, task);
        taskMapper.update(task);

        // 如果状态变化，记录日志
        if (!oldStatus.equals(dto.getStatus())) {
            recordLog(task.getId(), oldStatus, dto.getStatus(), "更新任务状态");
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findById(id);
        taskLogMapper.deleteByTaskId(id);
        taskMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(TaskStatusDTO dto) {
        Task task = findById(dto.getId());
        String oldStatus = task.getStatus();

        task.setStatus(dto.getStatus());
        if (dto.getActualTime() != null) {
            task.setActualTime(dto.getActualTime());
        }
        if ("COMPLETED".equals(dto.getStatus())) {
            task.setCompletedTime(LocalDateTime.now());
        }

        taskMapper.updateStatus(task);
        recordLog(task.getId(), oldStatus, dto.getStatus(), dto.getRemark());
    }

    @Override
    @Transactional
    public void startTask(Long id) {
        Task task = findById(id);
        if (!"NOT_STARTED".equals(task.getStatus())) {
            throw new BusinessException("只有未开始的任务才能开始");
        }

        String oldStatus = task.getStatus();
        task.setStatus("IN_PROGRESS");
        taskMapper.updateStatus(task);
        recordLog(id, oldStatus, "IN_PROGRESS", "开始任务");
    }

    @Override
    @Transactional
    public void completeTask(Long id, Integer actualTime) {
        Task task = findById(id);
        String oldStatus = task.getStatus();

        task.setStatus("COMPLETED");
        task.setActualTime(actualTime);
        task.setCompletedTime(LocalDateTime.now());

        taskMapper.updateStatus(task);
        recordLog(id, oldStatus, "COMPLETED", "完成任务");
    }

    private void copyDtoToEntity(TaskDTO dto, Task task) {
        task.setTaskName(dto.getTaskName());
        task.setPlanId(dto.getPlanId());
        task.setStageId(dto.getStageId());
        task.setTaskType(dto.getTaskType());
        task.setPriority(dto.getPriority());
        task.setEstimatedTime(dto.getEstimatedTime());
        task.setDeadline(dto.getDeadline());
        task.setStatus(dto.getStatus());
        task.setActualTime(dto.getActualTime());
        task.setRemark(dto.getRemark());
        task.setScheduledDate(dto.getScheduledDate());
    }

    private void recordLog(Long taskId, String fromStatus, String toStatus, String remark) {
        TaskLog log = new TaskLog();
        log.setTaskId(taskId);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setRemark(remark);
        taskLogMapper.insert(log);
    }
}
