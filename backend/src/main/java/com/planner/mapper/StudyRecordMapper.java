package com.planner.mapper;

import com.planner.entity.StudyRecord;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

/**
 * 学习记录Mapper
 */
@Mapper
public interface StudyRecordMapper {

    @Select("SELECT * FROM study_record WHERE user_id = #{userId} AND record_date = #{recordDate}")
    StudyRecord findByUserIdAndDate(@Param("userId") Long userId, @Param("recordDate") LocalDate recordDate);

    @Select("SELECT * FROM study_record WHERE user_id = #{userId} " +
            "AND record_date BETWEEN #{startDate} AND #{endDate} ORDER BY record_date")
    List<StudyRecord> findByDateRange(@Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Insert("INSERT INTO study_record (user_id, record_date, total_tasks, completed_tasks, total_time) " +
            "VALUES (#{userId}, #{recordDate}, #{totalTasks}, #{completedTasks}, #{totalTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StudyRecord record);

    @Update("UPDATE study_record SET total_tasks = #{totalTasks}, completed_tasks = #{completedTasks}, " +
            "total_time = #{totalTime} WHERE id = #{id}")
    int update(StudyRecord record);

    @Select("SELECT COUNT(*) FROM study_record WHERE user_id = #{userId} " +
            "AND completed_tasks > 0 AND record_date <= #{date} " +
            "ORDER BY record_date DESC")
    int countConsecutiveDays(@Param("userId") Long userId, @Param("date") LocalDate date);
}
