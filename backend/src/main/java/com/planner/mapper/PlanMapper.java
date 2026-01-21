package com.planner.mapper;

import com.planner.entity.Plan;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 学习方向Mapper
 */
@Mapper
public interface PlanMapper {

        @Select("SELECT * FROM [plan] WHERE user_id = #{userId} ORDER BY sort_order, priority DESC")
        List<Plan> findByUserId(Long userId);

        @Select("SELECT * FROM [plan] WHERE user_id = #{userId} AND status = 1 ORDER BY sort_order, priority DESC")
        List<Plan> findActiveByUserId(Long userId);

        @Select("SELECT * FROM [plan] WHERE id = #{id}")
        Plan findById(Long id);

        @Insert("INSERT INTO [plan] (user_id, plan_name, description, priority, color, status, sort_order) " +
                        "VALUES (#{userId}, #{planName}, #{description}, #{priority}, #{color}, #{status}, #{sortOrder})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(Plan plan);

        @Update("UPDATE [plan] SET plan_name = #{planName}, description = #{description}, " +
                        "priority = #{priority}, color = #{color}, status = #{status}, sort_order = #{sortOrder} " +
                        "WHERE id = #{id}")
        int update(Plan plan);

        @Delete("DELETE FROM [plan] WHERE id = #{id}")
        int deleteById(Long id);

        @Select("SELECT COUNT(*) FROM [plan] WHERE user_id = #{userId}")
        int countByUserId(Long userId);
}
