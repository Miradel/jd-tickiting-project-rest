package com.cybertek.aspect;



import org.aspectj.lang.JoinPoint;
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
public class LoggingAspect {

    Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    /*
     @Pointcut will declare where we gonna implement,
     ProductController.*(..) is mean ==> all the methods in this class
     @Before("pointcut()") ==> implements that method before all of the methods execution

     */


    @Pointcut("execution(* com.cybertek.controller.ProjectController.*(..)) || execution(* com.cybertek.controller.TaskController.*(..))")
    private void anyControllerOperation(){}

    @Before("anyControllerOperation()")
    public void anyBeforeControllerOperationAdvice(JoinPoint joinPoint){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Before(User : {} Method : {} - Parameters : {}",auth.getName(),joinPoint.getSignature().toShortString(),joinPoint.getArgs());
    }


    @AfterReturning(pointcut = "anyControllerOperation()",returning = "results")
    public void anyAfterReturningControllerOperationAdvice(JoinPoint joinPoint,Object results){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("AfterReturning(User : {} Method : {} - Results : {}",auth.getName(),joinPoint.getSignature().toShortString(),results.toString());
    }

    @AfterThrowing(pointcut = "anyControllerOperation()",throwing = "exception")
    public void anyAfterThrowingControllerOperationAdvice(JoinPoint joinPoint,RuntimeException exception){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("After throwing(User : {} Method : {} - Exception : {}",auth.getName(),joinPoint.getSignature().toShortString(),exception.getLocalizedMessage());

    }


//
//    // combination of above two methods
//    @Before("execution(* com.cybertek.controller.ProductController.*(..))")
//    public void beforeAdvice(){
//        logger.info("-----------");
//    }


//    // Execution
//    @Pointcut("execution(*  com.cybertek.controller.ProductController.up*(..))")
//    private void anyUpdateOperation(){
//
//    }
//
//    @Before("anyUpdateOperation()")
//    public void beforeControllerAdvice(JoinPoint joinPoint){
//        logger.info("Before -> Method {} - Arguments : {} - Target : {}",joinPoint,joinPoint.getArgs(),joinPoint.getTarget());
//    }
//
//    @Pointcut("execution(* com.cybertek.repository.ProductRepository.findById(Long))")
//    private void anyProductRepository(){
//
//    }
//
//    @Before("anyProductRepository()")
//    public void beforeProductRepoAdvice(JoinPoint joinPoint){
//        logger.info("Before(findById) -> Method {} - Arguments : {} - Target : {}",joinPoint,joinPoint.getArgs(),joinPoint.getTarget());
//    }
//
//
//
//    //Within ==> targeting in class level , @annotation ==> method level
//    @Pointcut("within(com.cybertek.controller..*)")
//    private void anyControllerOperation(){
//
//    }
//
//    @Pointcut("@within(org.springframework.stereotype.Service)")
//    private void anyServiceAnnotatedOperations(){
//
//    }
//
//    @Before("anyServiceAnnotatedOperations() || anyControllerOperation()")
//    public void beforeControllerAdvice2(JoinPoint joinPoint){
//        logger.info("Before -> Method {} - Arguments : {} - Target : {}",joinPoint,joinPoint.getArgs(),joinPoint.getTarget());
//    }
//
//    // Annotation
//    @Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
//    private void anyDeleteProductOperation(){}
//
//    @Before("anyDeleteProductOperation()")
//    public void beforeControllerAdvice3(JoinPoint joinPoint){
//        logger.info("Before -> Method {} - Arguments : {} - Target : {}",joinPoint,joinPoint.getArgs(),joinPoint.getTarget());
//    }
//
//
//    //after returning
//    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
//    private void anyGetProductOperation(){}
//
//    @AfterReturning(pointcut = "anyGetProductOperation()",returning = "results")
//    public void afterReturningControllerAdvice(JoinPoint joinPoint, Product results){
//        logger.info("After Returning(Mono Result) -> Method : {} - results :{}",joinPoint.getSignature().toShortString(),results);
//    }
//
//    @AfterReturning(pointcut = "anyGetProductOperation()",returning = "results")
//    public void afterReturningControllerAdvice2(JoinPoint joinPoint, List<Product> results){
//        logger.info("After Returning(List Result) -> Method : {} - results :{}",joinPoint.getSignature().toShortString(),results);
//    }
//
//
//    // After throwing
//    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
//    private void anyGetPutProductOperation(){}
//
//    @AfterThrowing(pointcut = "anyGetProductOperation()",throwing = "exception")
//    public void afterThrowingControllerAdvice(JoinPoint joinPoint, RuntimeException exception){
//        logger.info("After Throwing(Send Email to L2 Team) -> Method : {} - Exception :{}",
//                joinPoint.getSignature().toShortString(),exception.getMessage());
//    }
//
//    // After
//    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
//    private void anyGetPutProductOperation2(){}
//
//    @After( "anyGetPutProductOperation2()")
//    public void afterThrowingControllerAdvice(JoinPoint joinPoint){
//        logger.info("After Finally -> Method : {} - Exception :{}",
//                joinPoint.getSignature().toShortString());
//    }
//
//
//
//    // Around
//    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
//    private void anyPostProductOperation(){
//
//    }
//
//    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
//    private void anyPutProductOperation(){
//
//    }
//
//    @Around("anyPostProductOperation()")
//    public Object anyPostControllerAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        logger.info("Before(Method : {} - Parameters : {}",proceedingJoinPoint.getSignature().toShortString(),proceedingJoinPoint.getArgs());
//        List<Product> results = new ArrayList<>();
//        results =(List<Product>) proceedingJoinPoint.proceed();
//        logger.info("After(Method: {} - Results : {}",proceedingJoinPoint.getSignature().toShortString(),results);
//        return results;
//    }

}
