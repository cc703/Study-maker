package com.planner.mapper;

import com.planner.entity.TaskLog;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 任务日志Mapper
 */
@Mapper
public interface TaskLogMapper {

    @Select("SELECT * FROM task_log WHERE task_id = #{taskId} ORDER BY operation_time DESC")
    List<TaskLog> findByTaskId(Long taskId);

    @Insert("INSERT INTO task_log (task_id, from_status, to_status, remark) " +
            "VALUES (#{taskId}, #{fromStatus}, #{toStatus}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TaskLog taskLog);

    @Delete("DELETE FROM task_log WHERE task_id = #{taskId}")
    int deleteByTaskId(Long taskId);
}
