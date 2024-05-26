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
		log.info("OrderLoggingAspect - 사용자 ID : {} 의 주문을 처리합니다.", joinPoint.getArgs()[0]);

		Object result = joinPoint.proceed();

		log.info("OrderLoggingAspect - 사용자 ID : {} 의 주문 처리를 완료했습니다.", joinPoint.getArgs()[0]);
		return result;
	}

}
