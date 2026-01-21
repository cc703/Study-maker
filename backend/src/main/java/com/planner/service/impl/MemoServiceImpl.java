package com.planner.service.impl;

import com.planner.common.BusinessException;
import com.planner.dto.MemoDTO;
import com.planner.entity.Memo;
import com.planner.mapper.MemoMapper;
import com.planner.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 备忘录Service实现
 */
@Service
@RequiredArgsConstructor
public class MemoServiceImpl implements MemoService {

    private final MemoMapper memoMapper;

    private static final Long DEFAULT_USER_ID = 1L;

    @Override
    public List<Memo> findAll(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return memoMapper.findByUserId(DEFAULT_USER_ID);
        }
        return memoMapper.findByKeyword(DEFAULT_USER_ID, keyword.trim());
    }

    @Override
    public Memo findById(Long id) {
        Memo memo = memoMapper.findById(id);
        if (memo == null) {
            throw new BusinessException("备忘录不存在");
        }
        return memo;
    }

    @Override
    @Transactional
    public Long create(MemoDTO dto) {
        Memo memo = new Memo();
        memo.setUserId(DEFAULT_USER_ID);
        memo.setTitle(dto.getTitle());
        memo.setContent(dto.getContent());
        memo.setColor(dto.getColor());
        memo.setPinned(dto.getPinned());

        memoMapper.insert(memo);
        return memo.getId();
    }

    @Override
    @Transactional
    public void update(MemoDTO dto) {
        Memo memo = findById(dto.getId());
        memo.setTitle(dto.getTitle());
        memo.setContent(dto.getContent());
        memo.setColor(dto.getColor());
        memo.setPinned(dto.getPinned());

        memoMapper.update(memo);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findById(id);
        memoMapper.deleteById(id);
    }
}
