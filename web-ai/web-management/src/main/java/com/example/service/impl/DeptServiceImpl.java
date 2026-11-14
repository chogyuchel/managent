package com.example.service.impl;

import com.example.exception.ClazzDeleteException;
import com.example.exception.DeptDeleteException;
import com.example.mapper.DeptMapper;
import com.example.mapper.EmpMapper;
import com.example.pojo.Dept;
import com.example.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private EmpMapper empMapper;

    @Override
    public List<Dept> findAll() {
        return deptMapper.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        //1.查看部门是否有员工
        Integer count = empMapper.countByEmpId(id);

        if (count > 0){
            throw new DeptDeleteException("该部门下有员工，不能删除");
        }else{
            //2.删除部门
            deptMapper.deleteByid(id);
        }


    }

    @Override
    public void add(Dept dept) {
        //1.补全基础属性
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());

        //2.调用Mapper接口方法插入数据
        deptMapper.insert(dept);
    }

    @Override
    public Dept getById(Integer id) {
        return deptMapper.getById(id);
    }

    @Override
    public void update(Dept dept) {
        //1.补全基础属性
        dept.setUpdateTime(LocalDateTime.now());
        //2.调用Mapper接口方法更新部门
        deptMapper.update(dept);
    }
}
