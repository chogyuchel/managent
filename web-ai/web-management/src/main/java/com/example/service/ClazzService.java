package com.example.service;

import com.example.pojo.Clazz;
import com.example.pojo.ClazzQueryParam;
import com.example.pojo.PageResult;

import java.util.List;

public interface ClazzService {

    /**
     * 分页查询班级信息
     */
    PageResult<Clazz> page(ClazzQueryParam clazzQueryParam);

    void insert(Clazz clazz);

    Clazz getInfo(Integer id);

    void update(Clazz clazz);

    void delete(Integer id);

    List<Clazz> findAll();
}
