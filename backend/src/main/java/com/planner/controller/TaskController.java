package com.planner.controller;

import com.planner.common.PageResult;
import com.planner.common.Result;
import com.planner.dto.TaskDTO;
import com.planner.dto.TaskQueryDTO;
import com.planner.dto.TaskStatusDTO;
import com.planner.entity.Task;
import com.planner.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 任务Controller
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * 分页查询任务
     */
    @GetMapping
    public Result<PageResult<Task>> findByCondition(TaskQueryDTO query) {
        return Result.success(taskService.findByCondition(query));
    }

    /**
     * 获取今日任务
     */
    @GetMapping("/today")
    public Result<List<Task>> findTodayTasks() {
        return Result.success(taskService.findTodayTasks());
    }

    /**
     * 获取指定日期任务
     */
    @GetMapping("/date/{date}")
    public Result<List<Task>> findByDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return Result.success(taskService.findByScheduledDate(date));
    }

    /**
     * 获取截止日期任务
     */
    @GetMapping("/deadline/{date}")
    public Result<List<Task>> findByDeadline(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return Result.success(taskService.findByDeadline(date));
    }

    /**
     * 获取超期任务
     */
    @GetMapping("/overdue")
    public Result<List<Task>> findOverdueTasks() {
        return Result.success(taskService.findOverdueTasks());
    }

    /**
     * 根据ID获取任务
     */
    @GetMapping("/{id}")
    public Result<Task> findById(@PathVariable Long id) {
        return Result.success(taskService.findById(id));
    }

    /**
     * 新增任务
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody TaskDTO dto) {
        return Result.success("创建成功", taskService.create(dto));
    }

    /**
     * 更新任务
     */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody TaskDTO dto) {
        taskService.update(dto);
        return Result.success();
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return Result.success();
    }

    /**
     * 更新任务状态
     */
    @PutMapping("/status")
    public Result<Void> updateStatus(@Valid @RequestBody TaskStatusDTO dto) {
        taskService.updateStatus(dto);
        return Result.success();
    }

    /**
     * 开始任务
     */
    @PutMapping("/{id}/start")
    public Result<Void> startTask(@PathVariable Long id) {
        taskService.startTask(id);
        return Result.success();
    }

    /**
     * 完成任务
     */
    @PutMapping("/{id}/complete")
    public Result<Void> completeTask(@PathVariable Long id,
            @RequestParam(required = false) Integer actualTime) {
        taskService.completeTask(id, actualTime);
        return Result.success();
    }
}
