package com.example.controller;


import com.example.pojo.OperateLog;
import com.example.pojo.PageResult;
import com.example.pojo.Result;
import com.example.pojo.StudentQueryParam;
import com.example.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping("/page")
    public Result list(StudentQueryParam param){

        log.info("查询日志列表，参数：{}",param);
        PageResult<OperateLog> pageResult = logService.page(param);
        return Result.success(pageResult);

    }

}
