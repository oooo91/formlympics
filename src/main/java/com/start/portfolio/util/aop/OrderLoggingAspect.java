package com.start.portfolio.util.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class OrderLoggingAspect {

	@Pointcut("@annotation(com.start.portfolio.util.aop.LogAroundOrder)")
	public void logAroundOrderPointcut() {}

	@Around("logAroundOrderPointcut()")
	public Object logAroundOrder(ProceedingJoinPoint joinPoint) throws Throwable {
		log.info("OrderLoggingAspect - 사용자 ID : {} - {} 실행 시작", joinPoint.getArgs()[0], joinPoint.getSignature().getName());

		Object result = joinPoint.proceed();

		log.info("OrderLoggingAspect - 사용자 ID : {} - {} 실행 종료", joinPoint.getArgs()[0], joinPoint.getSignature().getName());
		return result;
	}

}
