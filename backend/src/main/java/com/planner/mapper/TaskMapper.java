package com.planner.mapper;

import com.planner.dto.TaskQueryDTO;
import com.planner.entity.Task;
import com.planner.vo.DailyStatVO;
import com.planner.vo.PlanStatVO;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

/**
 * 任务Mapper
 */
@Mapper
public interface TaskMapper {

        List<Task> findByCondition(TaskQueryDTO query);

        Long countByCondition(TaskQueryDTO query);

        @Select("SELECT t.*, p.plan_name, p.color as plan_color, s.stage_name FROM task t " +
                        "LEFT JOIN [plan] p ON t.plan_id = p.id " +
                        "LEFT JOIN stage s ON t.stage_id = s.id " +
                        "WHERE t.id = #{id}")
        Task findById(Long id);

        @Select("SELECT t.*, p.plan_name, p.color as plan_color, s.stage_name FROM task t " +
                        "LEFT JOIN [plan] p ON t.plan_id = p.id " +
                        "LEFT JOIN stage s ON t.stage_id = s.id " +
                        "WHERE t.scheduled_date = #{date} ORDER BY t.priority DESC, t.deadline")
        List<Task> findByScheduledDate(LocalDate date);

        @Select("SELECT t.*, p.plan_name, p.color as plan_color, s.stage_name FROM task t " +
                        "LEFT JOIN [plan] p ON t.plan_id = p.id " +
                        "LEFT JOIN stage s ON t.stage_id = s.id " +
                        "WHERE t.deadline = #{date} AND t.status != 'COMPLETED' ORDER BY t.priority DESC, t.deadline")
        List<Task> findByDeadline(LocalDate date);

        @Select("SELECT t.*, p.plan_name, p.color as plan_color, s.stage_name FROM task t " +
                        "LEFT JOIN [plan] p ON t.plan_id = p.id " +
                        "LEFT JOIN stage s ON t.stage_id = s.id " +
                        "WHERE t.deadline < #{date} AND t.status != 'COMPLETED' ORDER BY t.deadline")
        List<Task> findOverdueTasks(LocalDate date);

        @Insert("INSERT INTO task (task_name, plan_id, stage_id, task_type, priority, estimated_time, " +
                        "deadline, status, actual_time, remark, scheduled_date) " +
                        "VALUES (#{taskName}, #{planId}, #{stageId}, #{taskType}, #{priority}, #{estimatedTime}, " +
                        "#{deadline,jdbcType=DATE}, #{status}, #{actualTime}, #{remark}, #{scheduledDate,jdbcType=DATE})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(Task task);

        @Update("UPDATE task SET task_name = #{taskName}, plan_id = #{planId}, stage_id = #{stageId}, " +
                        "task_type = #{taskType}, priority = #{priority}, estimated_time = #{estimatedTime}, " +
                        "deadline = #{deadline,jdbcType=DATE}, status = #{status}, actual_time = #{actualTime}, " +
                        "remark = #{remark}, scheduled_date = #{scheduledDate,jdbcType=DATE} WHERE id = #{id}")
        int update(Task task);

        @Update("UPDATE task SET status = #{status}, actual_time = #{actualTime}, " +
                        "completed_time = #{completedTime,jdbcType=TIMESTAMP} WHERE id = #{id}")
        int updateStatus(Task task);

        @Delete("DELETE FROM task WHERE id = #{id}")
        int deleteById(Long id);

        @Delete("DELETE FROM task WHERE plan_id = #{planId}")
        int deleteByPlanId(Long planId);

        @Delete("DELETE FROM task WHERE stage_id = #{stageId}")
        int deleteByStageId(Long stageId);

        // 统计相关
        @Select("SELECT COUNT(*) FROM task WHERE scheduled_date = #{date}")
        int countByScheduledDate(LocalDate date);

        @Select("SELECT COUNT(*) FROM task WHERE scheduled_date = #{date} AND status = #{status}")
        int countByScheduledDateAndStatus(@Param("date") LocalDate date, @Param("status") String status);

        @Select("SELECT COUNT(*) FROM task WHERE status = 'COMPLETED'")
        int countCompleted();

        @Select("SELECT COUNT(*) FROM task")
        int countAll();

        List<PlanStatVO> getPlanStats();

        List<DailyStatVO> getWeeklyStats(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        List<DailyStatVO> getDailyStats(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
