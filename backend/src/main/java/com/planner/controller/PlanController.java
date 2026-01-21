package com.planner.controller;

import com.planner.common.Result;
import com.planner.dto.PlanDTO;
import com.planner.entity.Plan;
import com.planner.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学习方向Controller
 */
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    /**
     * 获取所有学习方向
     */
    @GetMapping
    public Result<List<Plan>> findAll() {
        return Result.success(planService.findAll());
    }

    /**
     * 获取启用的学习方向
     */
    @GetMapping("/active")
    public Result<List<Plan>> findActive() {
        return Result.success(planService.findActive());
    }

    /**
     * 根据ID获取学习方向
     */
    @GetMapping("/{id}")
    public Result<Plan> findById(@PathVariable Long id) {
        return Result.success(planService.findById(id));
    }

    /**
     * 新增学习方向
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody PlanDTO dto) {
        return Result.success("创建成功", planService.create(dto));
    }

    /**
     * 更新学习方向
     */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody PlanDTO dto) {
        planService.update(dto);
        return Result.success();
    }

    /**
     * 删除学习方向
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        planService.delete(id);
        return Result.success();
    }

    /**
     * 切换状态
     */
    @PutMapping("/{id}/toggle")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        planService.toggleStatus(id);
        return Result.success();
    }
}
