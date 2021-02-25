package com.cybertek.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;


@Aspect
@Configuration
public class PerformanceAspect {

    Logger log = LoggerFactory.getLogger(PerformanceAspect.class);
    @Pointcut("@annotation(com.cybertek.annotation.ExecutionTime)")
    private void anyExecutionTimeOperation(){}
    @Around("anyExecutionTimeOperation()")
    public Object anyExecutionTimeOperationAdvice(ProceedingJoinPoint proceedingJoinPoint)  {
        long beforeTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        long afterTime = System.currentTimeMillis();
        log.info("Time taken to execute : {} ms (Method : {} - Parameters : {}",(afterTime-beforeTime),proceedingJoinPoint.getSignature().toShortString(),proceedingJoinPoint.getArgs());
        return result;
    }

}
