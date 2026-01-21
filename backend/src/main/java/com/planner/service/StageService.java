package com.planner.service;

import com.planner.dto.StageDTO;
import com.planner.entity.Stage;
import java.util.List;

/**
 * 阶段Service接口
 */
public interface StageService {

    /**
     * 获取所有阶段
     */
    List<Stage> findAll();

    /**
     * 根据学习方向获取阶段
     */
    List<Stage> findByPlanId(Long planId);

    /**
     * 根据ID查询
     */
    Stage findById(Long id);

    /**
     * 新增阶段
     */
    Long create(StageDTO dto);

    /**
     * 更新阶段
     */
    void update(StageDTO dto);

    /**
     * 删除阶段
     */
    void delete(Long id);

    /**
     * 更新进度
     */
    void updateProgress(Long id, Integer progress);
}
