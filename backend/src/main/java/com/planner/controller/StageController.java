package com.planner.controller;

import com.planner.common.Result;
import com.planner.dto.StageDTO;
import com.planner.entity.Stage;
import com.planner.service.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 阶段Controller
 */
@RestController
@RequestMapping("/api/stages")
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;

    /**
     * 获取所有阶段
     */
    @GetMapping
    public Result<List<Stage>> findAll() {
        return Result.success(stageService.findAll());
    }

    /**
     * 根据学习方向获取阶段
     */
    @GetMapping("/plan/{planId}")
    public Result<List<Stage>> findByPlanId(@PathVariable Long planId) {
        return Result.success(stageService.findByPlanId(planId));
    }

    /**
     * 根据ID获取阶段
     */
    @GetMapping("/{id}")
    public Result<Stage> findById(@PathVariable Long id) {
        return Result.success(stageService.findById(id));
    }

    /**
     * 新增阶段
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody StageDTO dto) {
        return Result.success("创建成功", stageService.create(dto));
    }

    /**
     * 更新阶段
     */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody StageDTO dto) {
        stageService.update(dto);
        return Result.success();
    }

    /**
     * 删除阶段
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        stageService.delete(id);
        return Result.success();
    }

    /**
     * 更新进度
     */
    @PutMapping("/{id}/progress")
    public Result<Void> updateProgress(@PathVariable Long id, @RequestParam Integer progress) {
        stageService.updateProgress(id, progress);
        return Result.success();
    }
}
