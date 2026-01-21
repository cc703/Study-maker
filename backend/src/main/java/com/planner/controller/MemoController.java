package com.planner.controller;

import com.planner.common.Result;
import com.planner.dto.MemoDTO;
import com.planner.entity.Memo;
import com.planner.service.MemoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 备忘录Controller
 */
@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    /**
     * 获取备忘录列表
     */
    @GetMapping
    public Result<List<Memo>> findAll(@RequestParam(required = false) String keyword) {
        return Result.success(memoService.findAll(keyword));
    }

    /**
     * 根据ID获取备忘录
     */
    @GetMapping("/{id}")
    public Result<Memo> findById(@PathVariable Long id) {
        return Result.success(memoService.findById(id));
    }

    /**
     * 新增备忘录
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody MemoDTO dto) {
        return Result.success("创建成功", memoService.create(dto));
    }

    /**
     * 更新备忘录
     */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody MemoDTO dto) {
        memoService.update(dto);
        return Result.success();
    }

    /**
     * 删除备忘录
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        memoService.delete(id);
        return Result.success();
    }
}
