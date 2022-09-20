package com.example.springaopreentrantreadwritelock.autoproxy;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@NoArgsConstructor
@Slf4j
public class ReentrantLockAspect implements ObjectLock {
    @Pointcut("@annotation(com.example.springaopreentrantreadwritelock.autoproxy.ReentrantLockTransaction)")
    public void repositoryMethods() {}

    @Around("repositoryMethods()")
    public Object measureMethodExecutionTime(ProceedingJoinPoint pjp) throws Throwable {

        ReentrantReadWriteLock objectLock = getObjectLock(pjp);

        ReentrantLockTransaction reentrantLockTransactionAnnotation = getReentrantLockTransactionAnnotation(pjp);
        if (reentrantLockTransactionAnnotation.readOnly()) {
            LOGGER.info("before read lock");
            objectLock.readLock().lock();

            String methodName = pjp.getSignature().getName();
            LOGGER.info("before method: {} invocation", methodName);
            Object returnValue = pjp.proceed();
            LOGGER.info("after method: {} invocation", methodName);

            objectLock.readLock().unlock();
            LOGGER.info("after read unlock");

            return returnValue;
        } else {
            LOGGER.info("before write lock");
            objectLock.writeLock().lock();

            String methodName = pjp.getSignature().getName();
            LOGGER.info("before method: {} invocation", methodName);
            Object returnValue = pjp.proceed();
            LOGGER.info("after method: {} invocation", methodName);

            objectLock.writeLock().unlock();
            LOGGER.info("after write unlock");

            return returnValue;
        }
    }
}
