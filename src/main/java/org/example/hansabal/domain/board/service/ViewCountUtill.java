package org.example.hansabal.domain.board.service;


import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class ViewCountUtill {

    private final RedissonClient redissonClient;


    // 값 변경(조회수 증가)에 사용
    public void runWithLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit, Runnable runnable) {
        RLock lock = redissonClient.getLock(key);
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(waitTime, leaseTime, timeUnit);
            if (!isLocked) {
                throw new RuntimeException("Lock 획득 실패: " + key);
            }
            runnable.run();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // 값 반환(조회수 읽기)에 사용
    public <T> T executeWithLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(key);
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(waitTime, leaseTime, timeUnit);
            if (!isLocked) {
                throw new RuntimeException("Lock 획득 실패: " + key);
            }
            return supplier.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
