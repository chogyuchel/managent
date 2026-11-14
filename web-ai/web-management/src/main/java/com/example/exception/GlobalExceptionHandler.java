package com.example.exception;


import com.example.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
        log.error("程序出错了",e);
        return Result.error("出错了，请联系管理员");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result handleDuplicateKeyException(DuplicateKeyException e){
        log.error("程序出错了",e);
        String message = e.getMessage();
        int i = message.indexOf("Duplicate entry");
        String errMsg = message.substring(i);
        String[] arr = errMsg.split(" ");
        return Result.error(arr[2]+"已存在");
    }

    // 捕获你刚刚写的自定义异常
    @ExceptionHandler(ClazzDeleteException.class)
    public Result handleClazzNameDuplicateException(ClazzDeleteException e) {
        // 获取你在 throw 时写的报错信息，返回给前端
        return Result.error(e.getMessage());
    }

    // 捕获你刚刚写的自定义异常
    @ExceptionHandler(DeptDeleteException.class)
    public Result handleDeptNameDuplicateException(DeptDeleteException e) {
        // 获取你在 throw 时写的报错信息，返回给前端
        return Result.error(e.getMessage());
    }

}
