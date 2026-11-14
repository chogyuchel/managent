package com.example.service.impl;


import com.example.mapper.OperateLogMapper;
import com.example.pojo.Emp;
import com.example.pojo.OperateLog;
import com.example.pojo.PageResult;
import com.example.pojo.StudentQueryParam;
import com.example.service.LogService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private OperateLogMapper operateLogMapper;

    @Override
    public PageResult<OperateLog> page(StudentQueryParam param) {
        //1.设置分页参数
        PageHelper.startPage(param.getPage(),param.getPageSize());

        //2.执行查询
        List<OperateLog> logList = operateLogMapper.list(param);

        //3.解析查询结果，并封装
        Page<OperateLog> p = (Page<OperateLog>) logList;

        return new PageResult<OperateLog>(p.getTotal(),p.getResult());
    }
}
