package com.planner.service.impl;

import com.planner.common.BusinessException;
import com.planner.dto.PlanDTO;
import com.planner.entity.Plan;
import com.planner.mapper.PlanMapper;
import com.planner.mapper.StageMapper;
import com.planner.mapper.TaskMapper;
import com.planner.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 学习方向Service实现
 */
@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanMapper planMapper;
    private final StageMapper stageMapper;
    private final TaskMapper taskMapper;

    private static final Long DEFAULT_USER_ID = 1L;

    @Override
    public List<Plan> findAll() {
        return planMapper.findByUserId(DEFAULT_USER_ID);
    }

    @Override
    public List<Plan> findActive() {
        return planMapper.findActiveByUserId(DEFAULT_USER_ID);
    }

    @Override
    public Plan findById(Long id) {
        Plan plan = planMapper.findById(id);
        if (plan == null) {
            throw new BusinessException("学习方向不存在");
        }
        return plan;
    }

    @Override
    @Transactional
    public Long create(PlanDTO dto) {
        Plan plan = new Plan();
        plan.setUserId(DEFAULT_USER_ID);
        plan.setPlanName(dto.getPlanName());
        plan.setDescription(dto.getDescription());
        plan.setPriority(dto.getPriority());
        plan.setColor(dto.getColor());
        plan.setStatus(dto.getStatus());
        plan.setSortOrder(dto.getSortOrder());

        planMapper.insert(plan);
        return plan.getId();
    }

    @Override
    @Transactional
    public void update(PlanDTO dto) {
        Plan plan = findById(dto.getId());
        plan.setPlanName(dto.getPlanName());
        plan.setDescription(dto.getDescription());
        plan.setPriority(dto.getPriority());
        plan.setColor(dto.getColor());
        plan.setStatus(dto.getStatus());
        plan.setSortOrder(dto.getSortOrder());

        planMapper.update(plan);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findById(id);
        // 删除关联的任务
        taskMapper.deleteByPlanId(id);
        // 删除关联的阶段
        stageMapper.deleteByPlanId(id);
        // 删除学习方向
        planMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        Plan plan = findById(id);
        plan.setStatus(plan.getStatus() == 1 ? 0 : 1);
        planMapper.update(plan);
    }
}
