package com.planner.service;

import com.planner.dto.PlanDTO;
import com.planner.entity.Plan;
import java.util.List;

/**
 * 学习方向Service接口
 */
public interface PlanService {

    /**
     * 获取所有学习方向
     */
    List<Plan> findAll();

    /**
     * 获取启用的学习方向
     */
    List<Plan> findActive();

    /**
     * 根据ID查询
     */
    Plan findById(Long id);

    /**
     * 新增学习方向
     */
    Long create(PlanDTO dto);

    /**
     * 更新学习方向
     */
    void update(PlanDTO dto);

    /**
     * 删除学习方向
     */
    void delete(Long id);

    /**
     * 切换状态
     */
    void toggleStatus(Long id);
}
