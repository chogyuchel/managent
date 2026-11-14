package com.example.controller;


import com.example.pojo.PageResult;
import com.example.pojo.Result;
import com.example.pojo.Student;
import com.example.pojo.StudentQueryParam;
import com.example.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 分页查询学生
     */
    @GetMapping
    public Result PageGet(StudentQueryParam studentQueryParam){

        log.info("分页查询学生{}",studentQueryParam);
        PageResult<Student> pageResult = studentService.page(studentQueryParam);
        return Result.success(pageResult);

    }

    /**
     * 新增学生
     */
    @PostMapping
    public Result insert(@RequestBody Student student){

        log.info("新增学生{}",student);
        studentService.insert(student);
        return Result.success();

    }

    /**
     * 根据id查询学生信息
     */
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id){

        log.info("根据id查询学生信息{}",id);
        Student student = studentService.getInfo(id);
        return Result.success(student);

    }

    /**
     * 修改学生信息
     */
    @PutMapping
    public Result updateStudnet(@RequestBody Student student){

        log.info("修改学生信息{}",student);
        studentService.update(student);
        return Result.success();

    }

    /**
     * 删除学员
     */
    @DeleteMapping("/{ids}")
    public Result delete(@PathVariable List<Integer> ids){

        log.info("删除学生{}",ids);
        studentService.delete(ids);
        return Result.success();

    }

    /**
     * 违纪处理
     */
    @PutMapping("/violation/{id}/{score}")
    public Result violation(@PathVariable Integer id,@PathVariable Integer score){

        log.info("违纪处理{},{}",id,score);
        studentService.violation(id,score);
        return Result.success();

    }
}
