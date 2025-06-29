package com.gr.geias.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 人员信息删除切面
 */
@Aspect
@Component
public class PersonFase {
    private final String POINT_CUT = "execution(* com.gr.geias.repository.PersonInfoRepository.delPersonById(..))";
    
    @Pointcut(POINT_CUT)
    private void pointcut() {}

    @After(value = POINT_CUT)
    public void doAfterAdvice(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Integer personId = (Integer)args[0];
        System.out.println("删除用户ID: " + personId);
    }
}
