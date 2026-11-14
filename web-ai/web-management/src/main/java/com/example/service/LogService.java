package com.example.service;

import com.example.pojo.OperateLog;
import com.example.pojo.PageResult;
import com.example.pojo.StudentQueryParam;
import org.springframework.stereotype.Service;

@Service
public interface LogService {


    PageResult<OperateLog> page(StudentQueryParam param);
}
