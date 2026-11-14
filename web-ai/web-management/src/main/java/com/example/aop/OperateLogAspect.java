package com.example.aop;

import com.example.mapper.OperateLogMapper;
import com.example.pojo.OperateLog;
import com.example.utils.CurrentHolder;
import com.example.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 操作日志切面：记录 com.example.controller 包下所有“增、删、改”接口的操作日志
 */
@Slf4j
@Aspect
@Component
public class OperateLogAspect {

    @Autowired
    private OperateLogMapper operateLogMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 切入点：com.example.controller 包下所有方法，且必须是 增/删/改（POST/PUT/DELETE）
     * 查询（GET）接口不记录
     */
    @Pointcut("execution(* com.example.controller..*(..)) && " +
            "(@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
             "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
             "@annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public void operateLogPointcut() {
    }

    /**
     * 环绕通知：在目标方法前后记录耗时、参数、返回值，并落库
     */
    @Around("operateLogPointcut()")
    public Object recordOperateLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long begin = System.currentTimeMillis();

        // 执行目标方法
        Object result = joinPoint.proceed();

        long costTime = System.currentTimeMillis() - begin;

        // 记录日志（日志写入失败不影响主业务）
        try {
            saveLog(joinPoint, result, costTime);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }

        return result;
    }

    private void saveLog(ProceedingJoinPoint joinPoint, Object result, long costTime) throws Exception {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // 目标类的全类名、方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();

        // 方法运行时参数、返回值（JSON 序列化）
        String methodParams = toJson(joinPoint.getArgs());
        String returnValue = toJson(result);

        // 操作人 ID（从 JWT 中解析）
        Integer operateEmpId = getCurrentEmpId();
        //删除ThreadLocal中的数据
        CurrentHolder.remove();

        OperateLog operateLog = new OperateLog();
        operateLog.setOperateEmpId(operateEmpId);
        operateLog.setOperateTime(LocalDateTime.now());
        operateLog.setClassName(className);
        operateLog.setMethodName(methodName);
        operateLog.setMethodParams(methodParams);
        operateLog.setReturnValue(returnValue);
        operateLog.setCostTime(costTime);

        operateLogMapper.insert(operateLog);
    }

    /**
     * 从请求头 token 中解析当前登录员工的 ID
     */
    private Integer getCurrentEmpId() {
        return CurrentHolder.getCurrentId();
    }

    /**
     * 序列化为 JSON；若某些参数无法序列化（如文件上传的 MultipartFile），降级为字符串
     */
    private String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            if (obj instanceof Object[]) {
                return Arrays.toString((Object[]) obj);
            }
            return obj.toString();
        }
    }
}
