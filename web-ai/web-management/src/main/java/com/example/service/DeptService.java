package com.example.service;


import com.example.pojo.Dept;

import java.util.List;

public interface DeptService {
    /**
     * 查询所有的部门数据
     */
    List<Dept> findAll();

    /**
     * 根据部门id删除
     *
     */
    void deleteById(Integer id);

    /**
     * 添加部门
     */
    void add(Dept dept);

    Dept getById(Integer id);

    void update(Dept dept);
}
