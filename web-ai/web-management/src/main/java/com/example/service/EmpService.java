package com.example.service;

import com.example.pojo.Emp;
import com.example.pojo.EmpQueryParam;
import com.example.pojo.LoginInfo;
import com.example.pojo.PageResult;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public interface EmpService {

    /**
     * 分页查询
     * @return
     */
//    PageResult<Emp> page(Integer page, Integer pageSize,String name, Integer gender, LocalDate begin, LocalDate end);
    PageResult<Emp> page(EmpQueryParam empQueryParam);

    /**
     * 新增员工
     */
    void save(Emp emp);

    /**
     * 删除员工
     */
    void delete(List<Integer> ids);

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    Emp getInfo(Integer id);

    /**
     * 修改员工
     */
    void update(Emp emp);

    /**
     * 查询所有员工
     */
    List<Emp> findAll();

    /**
     * 登陆
     */
    LoginInfo login(Emp emp);
}
