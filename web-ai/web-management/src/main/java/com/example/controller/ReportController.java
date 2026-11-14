package com.example.controller;


import com.example.pojo.ClazzOption;
import com.example.pojo.JobOption;
import com.example.pojo.Result;
import com.example.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 获取员工职位人数
     */
    @GetMapping("/empJobData")
    public Result getEmpJobData(){
        log.info("获取员工职位人数");
        JobOption jobOption = reportService.getEmpJobData();
        return Result.success(jobOption);
    }

    /**
     * 统计员工性别人数
     * @return
     */
    @GetMapping("/empGenderData")
    public Result getEmpGenderDate(){
        log.info("统计员工性别人数");
        List<Map<String,Object>> genderList = reportService.getEmpGenderData();
        return Result.success(genderList);
    }

    /**
     * 统计班级人数
     */
    @GetMapping("/studentCountData")
    public Result getClazzStudentDate(){

        log.info("统计班级人数");
        ClazzOption clazzOption = reportService.getClazzStudentData();
        return Result.success(clazzOption);

    }

    /**
     * 统计学生学历
     */
    @GetMapping("/studentDegreeData")
    public Result getStudentDegree(){

        log.info("统计学生学历");
        List<Map<String,Object>> degreeList = reportService.getStudentDegreeData();
        return Result.success(degreeList);

    }

}
