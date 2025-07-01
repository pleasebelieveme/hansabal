package org.example.hansabal.common.redisson;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.exception.CommonErrorCode;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class DistributedLockAop {
	private static final String REDISSON_LOCK_PREFIX = "LOCK:";

	private final RedissonClient redissonClient;
	private final AopForTransaction aopForTransaction;

	@Around("@annotation(org.example.hansabal.common.redisson.DistributedLock)")
	public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

		String key = REDISSON_LOCK_PREFIX +
			CustomSpringELParser.getDynamicValue(
				signature.getParameterNames(),
				joinPoint.getArgs(),
				distributedLock.key());
		log.info("lock on [method:{}] [key:{}]", method, key);

		RLock rLock = redissonClient.getLock(key);
		String lockName = rLock.getName();
		try {
			boolean available =
				rLock.tryLock(
					distributedLock.waitTime(),
					distributedLock.leaseTime(),
					distributedLock.timeUnit());
			if (!available) {
				throw new BizException(CommonErrorCode.LOCK_FAILED);
			}

			return aopForTransaction.proceed(joinPoint);

		} catch (InterruptedException e) {//락을 얻으려고 시도하다가 인터럽트를 받았을 때 발생
			throw new BizException(CommonErrorCode.LOCK_INTERRUPTED);
		} finally {
			try {
				rLock.unlock();
				log.info("unlock complete [Lock:{}] ", lockName);
			} catch (IllegalMonitorStateException e) {//락이 이미 종료되었을때 발생
				log.info("Redisson Lock Already Unlocked");
			}
		}
	}
}
