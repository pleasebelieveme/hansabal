package org.example.hansabal.common.redisson;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
	/**
	 * 락의 이름
	 */
	String key();

	/**
	 * 락의 시간 단위
	 */
	TimeUnit timeUnit() default TimeUnit.SECONDS;

	/**
	 * 락을 기다리는 시간 (default - 5s)
	 * 락 획득을 위해 waitTime 만큼 대기한다
	 */
	long waitTime() default 5L;

	/**
	 * 락 임대 시간 (default - 3s)
	 * 락을 획득한 이후 leaseTime 이 지나면 락을 해제한다
	 */
	long leaseTime() default 3L;

	/** 사용법
	 *  @DistributedLock(key = "'엔티티이름(전체 대문자):' + #엔티티 이름(전체 소문자).id")
	 *  예시 -> @DistributedLock(key = "'FOOD:' + #food.id")
	 */

}
