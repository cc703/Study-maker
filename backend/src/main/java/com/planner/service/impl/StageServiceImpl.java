package com.planner.service.impl;

import com.planner.common.BusinessException;
import com.planner.dto.StageDTO;
import com.planner.entity.Stage;
import com.planner.mapper.StageMapper;
import com.planner.mapper.TaskMapper;
import com.planner.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 阶段Service实现
 */
@Service
@RequiredArgsConstructor
public class StageServiceImpl implements StageService {

    private final StageMapper stageMapper;
    private final TaskMapper taskMapper;

    @Override
    public List<Stage> findAll() {
        return stageMapper.findAll();
    }

    @Override
    public List<Stage> findByPlanId(Long planId) {
        return stageMapper.findByPlanId(planId);
    }

    @Override
    public Stage findById(Long id) {
        Stage stage = stageMapper.findById(id);
        if (stage == null) {
            throw new BusinessException("阶段不存在");
        }
        return stage;
    }

    @Override
    @Transactional
    public Long create(StageDTO dto) {
        Stage stage = new Stage();
        stage.setPlanId(dto.getPlanId());
        stage.setStageName(dto.getStageName());
        stage.setDescription(dto.getDescription());
        stage.setStartDate(dto.getStartDate());
        stage.setEndDate(dto.getEndDate());
        stage.setTarget(dto.getTarget());
        stage.setStatus(dto.getStatus());
        stage.setProgress(dto.getProgress());
        stage.setSortOrder(dto.getSortOrder());

        stageMapper.insert(stage);
        return stage.getId();
    }

    @Override
    @Transactional
    public void update(StageDTO dto) {
        Stage stage = findById(dto.getId());
        stage.setPlanId(dto.getPlanId());
        stage.setStageName(dto.getStageName());
        stage.setDescription(dto.getDescription());
        stage.setStartDate(dto.getStartDate());
        stage.setEndDate(dto.getEndDate());
        stage.setTarget(dto.getTarget());
        stage.setStatus(dto.getStatus());
        stage.setProgress(dto.getProgress());
        stage.setSortOrder(dto.getSortOrder());

        stageMapper.update(stage);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findById(id);
        // 删除关联的任务
        taskMapper.deleteByStageId(id);
        // 删除阶段
        stageMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateProgress(Long id, Integer progress) {
        findById(id);
        stageMapper.updateProgress(id, progress);
    }
}
