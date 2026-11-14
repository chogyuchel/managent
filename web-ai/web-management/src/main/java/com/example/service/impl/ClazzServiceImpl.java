package com.example.service.impl;

import com.example.exception.ClazzDeleteException;
import com.example.mapper.ClazzMapper;
import com.example.mapper.StudentMapper;
import com.example.pojo.Clazz;
import com.example.pojo.ClazzQueryParam;
import com.example.pojo.PageResult;
import com.example.service.ClazzService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClazzServiceImpl implements ClazzService {

    @Autowired
    private ClazzMapper clazzMapper;

    @Autowired
    private StudentMapper studentMapper;

    /**
     * 分页查询班级
     */
    @Override
    public PageResult<Clazz> page(ClazzQueryParam clazzQueryParam) {
        //1.设置分页参数
        PageHelper.startPage(clazzQueryParam.getPage(),clazzQueryParam.getPageSize());

        //2.执行查询
        List<Clazz> clazzList = clazzMapper.list(clazzQueryParam);

        //3.解析查询结果，并封装
        Page<Clazz> p = (Page<Clazz>) clazzList;

        return new PageResult<Clazz>(p.getTotal(),clazzList);
    }

    /**
     * 新增班级
     */
    @Override
    public void insert(Clazz clazz) {
        //1.设置创建时间和修改时间
        clazz.setCreateTime(LocalDateTime.now());
        clazz.setUpdateTime(LocalDateTime.now());
        //2.执行插入操作
        clazzMapper.insert(clazz);
    }

    /**
     * 根据id查询班级信息
     */
    @Override
    public Clazz getInfo(Integer id) {
        return clazzMapper.getById(id);
    }

    /**
     * 修改班级信息
     */
    @Override
    public void update(Clazz clazz) {
        //1.设置修改时间
        clazz.setUpdateTime(LocalDateTime.now());
        //2.执行修改操作
        clazzMapper.update(clazz);
    }

    /**
     * 删除班级
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Integer id) {
        //1.查询班级是否有学员
        Integer count = studentMapper.countByClazzId(id);
        if (count > 0){
            throw new ClazzDeleteException("该班级下有学员，不能删除");
        }else{
            //2.删除班级
            clazzMapper.deleteByid(id);
        }

    }

    /**
     * 查询所有班级
     */
    @Override
    public List<Clazz> findAll() {
        return clazzMapper.findAll();
    }
}
