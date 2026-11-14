package com.example.mapper;

import com.example.pojo.Clazz;
import com.example.pojo.ClazzQueryParam;
import com.example.pojo.PageResult;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface ClazzMapper {


    /**
     * 分页查询班级
     */
    List<Clazz> list(ClazzQueryParam clazzQueryParam);

    /**
     * 新增班级
     */
    @Insert("insert into clazz(name,room,begin_date,end_date,master_id,subject,create_time,update_time)" +
            " values (#{name},#{room},#{beginDate},#{endDate},#{masterId},#{subject},#{createTime},#{updateTime})")
    void insert(Clazz clazz);

    /**
     * 根据id查询班级信息
     */
    @Select("select id,name,room,begin_date,end_date,master_id,subject,create_time,update_time from clazz where id = #{id}")
    Clazz getById(Integer id);

    /**
     * 修改班级信息
     */
    void update(Clazz clazz);

    /**
     * 根据id删除班级
     */
    @Delete("delete from clazz where id = #{id}")
    void deleteByid(Integer id);

    /**
     * 查询所有班级
     */
    @Select("select id, name, room, begin_date, end_date, master_id, subject, create_time, update_time from clazz")
    List<Clazz> findAll();
}
