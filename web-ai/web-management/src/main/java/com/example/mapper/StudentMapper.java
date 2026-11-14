package com.example.mapper;

import com.example.pojo.Student;
import com.example.pojo.StudentQueryParam;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper {



    /**
     * 根据班级id查询学生数量
     */
    @Select("select count(*) from student where clazz_id = #{id}")
    Integer countByClazzId(Integer id);

    List<Student> list(StudentQueryParam studentQueryParam);

    /**
     * 新增学生
     */
    @Insert("insert student(name,no,gender,phone,id_card,is_college,address,degree,graduation_date,clazz_id," +
            "create_time,update_time) VALUES (#{name},#{no},#{gender},#{phone},#{idCard},#{isCollege}," +
            "#{address},#{degree},#{graduationDate},#{clazzId},#{createTime},#{updateTime})")
    void insert(Student student);

    /**
     * 根据id查询学生信息
     */
    @Select("select s.*,c.id clazzId from student s left join clazz c on s.clazz_id = c.id where s.id = #{id}")
    Student getById(Integer id);

    /**
     * 修改学生信息
     */
    void update(Student student);

    /**
     * 根据id批量删除学生信息
     */
    void deleteByIds(List<Integer> ids);

    /**
     * 违纪
     */
    @Update("update student set violation_count = violation_count + 1 , violation_score = violation_score +" +
            " #{score} where id = #{id}")
    void violation(Integer id, Integer score);

    List<Map<String, Object>> countClazzStudentData();
}
