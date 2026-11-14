package com.example.service;

import com.example.pojo.ClazzOption;
import com.example.pojo.JobOption;

import java.util.List;
import java.util.Map;

public interface ReportService {

    /**
     * 获取员工职位人数
     */
    JobOption getEmpJobData();

    /**
     * 统计员工性别人数
     * @return
     */
    List<Map<String, Object>> getEmpGenderData();

    /**
     * 统计班级人数
     */
    ClazzOption getClazzStudentData();

    List<Map<String, Object>> getStudentDegreeData();
}
