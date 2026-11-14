package com.example.service;


import com.example.pojo.PageResult;
import com.example.pojo.Student;
import com.example.pojo.StudentQueryParam;

import java.util.List;


public interface StudentService {

    /**
     *分页查询学生
     */
    PageResult<Student> page(StudentQueryParam studentQueryParam);

    /**
     * 新增学生
     */
    void insert(Student student);

    /**
     * 根据id查询学生信息
     */
    Student getInfo(Integer id);

    /**
     * 修改学生信息
     */
    void update(Student student);

    /**
     * 删除学生信息
     */
    void delete(List<Integer> ids);

    /**
     * 违纪
     */
    void violation(Integer id, Integer score);
}
