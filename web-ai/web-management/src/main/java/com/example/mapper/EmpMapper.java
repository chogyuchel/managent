package com.example.mapper;

import com.example.pojo.Emp;
import com.example.pojo.EmpQueryParam;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 员工信息
 */
@Mapper
public interface EmpMapper {

    List<Emp> findAll();


    //------------------------------原始分页查询----------------------------
    /**
     * 查询总记录数
     */
//    @Select("select count(*) from emp e left join dept d on e.dept_id = d.id")
//    public Long count();

    /**
     * 分页查询
     */
//    @Select("select e.*,d.name deptName from emp e left join dept d on e.dept_id = d.id " +
//            "order by e.update_time desc limit #{start},#{pageSize}")
//    public List<Emp> list(Integer start, Integer pageSize);

//    @Select("select e.*,d.name deptName from emp e left join dept d on e.dept_id = d.id order by e.update_time desc")
//    public List<Emp> list(String name, Integer gender, LocalDate begin, LocalDate end);
    public List<Emp> list(EmpQueryParam empQueryParam);

    /**
     * 新增员工基本信息
     */
    @Options(useGeneratedKeys = true,keyProperty = "id") //获取到生成的主键 -- 主键返回
    @Insert("insert into emp(username,name,gender,phone,job,salary,image,entry_date,dept_id,create_time,update_time)" +
            "values (#{username},#{name},#{gender},#{phone},#{job},#{salary},#{image},#{entryDate},#{deptId},#{createTime},#{updateTime})")
    void insert(Emp emp);

    /**
     * 根据id批量删除员工的基本信息
     */
    void deleteByIds(List<Integer> ids);

    /**
     * 根据id查询员工信息
     */
    Emp getById(Integer id);

    /**
     * 根据id修改员工信息
     */
    void updateById(Emp emp);

    /**
     * 统计员工职位人数
     */
    List<Map<String,Object>> countEmpJobData();

    /**
     * 统计员工性别人数
     */
    List<Map<String, Object>> countEmpGenderData();

    /**
     * 统计员工学历
     */
    List<Map<String, Object>> getStudentDegreeData();

    /**
     * 统计员工学历
     */
    @Select("select count(*) from emp where dept_id = #{id}")
    Integer countByEmpId(Integer id);

    /**
     * 根据用户名和密码查询员工信息
     */
    @Select("select id,username,name from emp where username = #{username} and password = #{password}")
    Emp selectByUsernameAndPassword(Emp emp);
}
