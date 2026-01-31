package com.javastudy.config.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.my.loger.api.LogSpan;

@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class MethodSpanAspect {

	@Around("within(com.javastudy..*) && !within(com.javastudy.config..*)")
	public Object around(final ProceedingJoinPoint pjp) throws Throwable {

		final Logger log = LoggerFactory.getLogger(pjp.getSignature().getDeclaringType());
		final String label = pjp.getSignature().getDeclaringTypeName()
			+ "#" + pjp.getSignature().getName();

		final LogSpan span = LogSpan.logStart(log, label);
		try {
			return pjp.proceed();
		} catch (final Throwable t) {
			span.logException(t); // ★追加：例外時はここで “終わらせる”
			throw t;
		} finally {
			span.logEnd(); // ★二重終了はNOOP化しておくと安全
		}
	}
}
