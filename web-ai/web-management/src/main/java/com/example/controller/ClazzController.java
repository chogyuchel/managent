package com.example.controller;

import com.example.pojo.Clazz;
import com.example.pojo.ClazzQueryParam;
import com.example.pojo.PageResult;
import com.example.pojo.Result;
import com.example.service.ClazzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/clazzs")
public class ClazzController {

    @Autowired
    private ClazzService clazzService;

    /**
     * 分页查询班级
     */
    @GetMapping
    public Result list(ClazzQueryParam clazzQueryParam){
        log.info("分页查询班级{}",clazzQueryParam);
        PageResult<Clazz> pageResult = clazzService.page(clazzQueryParam);
        return Result.success(pageResult);
    }

    /**
     * 新增班级
     */
    @PostMapping
    public Result insert(@RequestBody Clazz clazz){
        log.info("新增班级{}",clazz);
        clazzService.insert(clazz);
        return Result.success();
    }

    /**
     * 根据id查询班级信息
     */
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id){
        log.info("根据id查询班级信息{}",id);
        Clazz clazz = clazzService.getInfo(id);
        return Result.success(clazz);
    }

    /**
     * 修改班级信息
     */
    @PutMapping
    public Result updateClazz(@RequestBody Clazz clazz){
        log.info("修改班级信息{}",clazz);
        clazzService.update(clazz);
        return Result.success();
    }

    /**
     * 删除班级
     */
    @DeleteMapping("/{id}")
    public Result deleteClazz(@PathVariable Integer id){
        log.info("删除班级{}",id);
        clazzService.delete(id);
        return Result.success();
    }

    /**
     * 查询所有班级
     */
    @GetMapping("/list")
    public Result findall(){

        log.info("查询所有班级");
        List<Clazz> clazzList = clazzService.findAll();
        return Result.success(clazzList);

    }

}
