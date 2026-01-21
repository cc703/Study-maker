package com.planner.mapper;

import com.planner.entity.Stage;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 阶段Mapper
 */
@Mapper
public interface StageMapper {

        @Select("SELECT s.*, p.plan_name, p.color as plan_color FROM stage s " +
                        "LEFT JOIN [plan] p ON s.plan_id = p.id " +
                        "WHERE s.plan_id = #{planId} ORDER BY s.sort_order, s.start_date")
        List<Stage> findByPlanId(Long planId);

        @Select("SELECT s.*, p.plan_name, p.color as plan_color FROM stage s " +
                        "LEFT JOIN [plan] p ON s.plan_id = p.id " +
                        "ORDER BY s.sort_order, s.start_date")
        List<Stage> findAll();

        @Select("SELECT s.*, p.plan_name, p.color as plan_color FROM stage s " +
                        "LEFT JOIN [plan] p ON s.plan_id = p.id " +
                        "WHERE s.id = #{id}")
        Stage findById(Long id);

        @Insert("INSERT INTO stage (plan_id, stage_name, description, start_date, end_date, target, status, progress, sort_order) "
                        +
                        "VALUES (#{planId}, #{stageName}, #{description}, #{startDate,jdbcType=DATE}, #{endDate,jdbcType=DATE}, #{target}, #{status}, #{progress}, #{sortOrder})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(Stage stage);

        @Update("UPDATE stage SET plan_id = #{planId}, stage_name = #{stageName}, description = #{description}, " +
                        "start_date = #{startDate,jdbcType=DATE}, end_date = #{endDate,jdbcType=DATE}, target = #{target}, "
                        +
                        "status = #{status}, progress = #{progress}, sort_order = #{sortOrder} WHERE id = #{id}")
        int update(Stage stage);

        @Delete("DELETE FROM stage WHERE id = #{id}")
        int deleteById(Long id);

        @Delete("DELETE FROM stage WHERE plan_id = #{planId}")
        int deleteByPlanId(Long planId);

        @Update("UPDATE stage SET progress = #{progress} WHERE id = #{id}")
        int updateProgress(@Param("id") Long id, @Param("progress") Integer progress);
}
