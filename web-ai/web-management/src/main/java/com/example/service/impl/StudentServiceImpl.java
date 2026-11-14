package com.example.service.impl;

import com.example.mapper.StudentMapper;
import com.example.pojo.PageResult;
import com.example.pojo.Student;
import com.example.pojo.StudentQueryParam;
import com.example.service.StudentService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    /**
     * 分页查询学生
     */
    @Override
    public PageResult<Student> page(StudentQueryParam studentQueryParam) {
        //1.设置分页参数
        PageHelper.startPage(studentQueryParam.getPage(),studentQueryParam.getPageSize());

        //2.执行查询
        List<Student> studentList = studentMapper.list(studentQueryParam);

        //3.封装数据
        Page<Student> p = (Page<Student>) studentList;

        return new PageResult<Student>(p.getTotal(),studentList);
    }

    /**
     * 新增学生
     */
    @Override
    public void insert(Student student) {
        //1.设置创建时间和修改时间
        student.setCreateTime(LocalDateTime.now());
        student.setUpdateTime(LocalDateTime.now());
        //2.执行插入操作
        studentMapper.insert(student);
    }

    /**
     * 根据id查询学生信息
     */
    @Override
    public Student getInfo(Integer id) {
        return studentMapper.getById(id);
    }

    /**
     * 修改学生数据
     */
    @Override
    public void update(Student student) {
        //1.写入修改时间
        student.setUpdateTime(LocalDateTime.now());
        //2.执行修改操作
        studentMapper.update(student);
    }

    /**
     * 删除学生
     */
    @Override
    public void delete(List<Integer> ids) {
        studentMapper.deleteByIds(ids);
    }

    /**
     * 违纪处理
     */
    @Override
    public void violation(Integer id, Integer score) {
        studentMapper.violation(id,score);
    }
}
