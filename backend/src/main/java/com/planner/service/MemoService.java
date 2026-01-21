package com.planner.service;

import com.planner.dto.MemoDTO;
import com.planner.entity.Memo;
import java.util.List;

/**
 * 备忘录Service接口
 */
public interface MemoService {

    /**
     * 获取所有备忘录
     */
    List<Memo> findAll(String keyword);

    /**
     * 根据ID查询
     */
    Memo findById(Long id);

    /**
     * 新增备忘录
     */
    Long create(MemoDTO dto);

    /**
     * 更新备忘录
     */
    void update(MemoDTO dto);

    /**
     * 删除备忘录
     */
    void delete(Long id);
}
