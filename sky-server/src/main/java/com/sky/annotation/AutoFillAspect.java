package com.sky.annotation;

import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){};
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws Throwable {
       //获取当前操作类型  通过方法上注解获得
        MethodSignature methodSignature =(MethodSignature)joinPoint.getSignature();

        Method method = methodSignature.getMethod();
        AutoFill autoFill = method.getAnnotation(AutoFill.class);
        OperationType operationType= autoFill.value();


        //获得当前处理的对象
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }
        Object entity=args[0];

        //准备要填充的对象
        LocalDateTime now=LocalDateTime.now();
        Long id= BaseContext.getCurrentId();

        //根据操作类型 通过反射为实体对象对不同字段赋zhi
        if (operationType.equals(OperationType.INSERT)){
            entity.getClass().getMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class).invoke(entity,now);
            entity.getClass().getMethod(AutoFillConstant.SET_CREATE_USER,Long.class).invoke(entity,id);
        }
        entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class).invoke(entity,now);
        entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER,Long.class).invoke(entity,id);


    }
}
