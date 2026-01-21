package com.planner.mapper;

import com.planner.entity.Memo;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 备忘录Mapper
 */
@Mapper
public interface MemoMapper {

    @Select("SELECT * FROM memo WHERE user_id = #{userId} ORDER BY pinned DESC, update_time DESC")
    List<Memo> findByUserId(Long userId);

    @Select("SELECT * FROM memo WHERE id = #{id}")
    Memo findById(Long id);

    @Select("SELECT * FROM memo WHERE user_id = #{userId} AND (title LIKE '%' + #{keyword} + '%' OR content LIKE '%' + #{keyword} + '%') "
            +
            "ORDER BY pinned DESC, update_time DESC")
    List<Memo> findByKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);

    @Insert("INSERT INTO memo (user_id, title, content, color, pinned) " +
            "VALUES (#{userId}, #{title}, #{content}, #{color}, #{pinned})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Memo memo);

    @Update("UPDATE memo SET title = #{title}, content = #{content}, color = #{color}, pinned = #{pinned} WHERE id = #{id}")
    int update(Memo memo);

    @Delete("DELETE FROM memo WHERE id = #{id}")
    int deleteById(Long id);
}
