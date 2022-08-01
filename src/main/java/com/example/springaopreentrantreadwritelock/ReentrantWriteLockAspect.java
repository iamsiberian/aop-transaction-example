package com.example.springaopreentrantreadwritelock;

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
public class ReentrantWriteLockAspect implements ObjectLock {

    //Pointcut("@target(com.example.springaopreentrantreadwritelock.ReentrantWriteLock)")
    @Pointcut("@annotation(ReentrantWriteLock)")
    public void repositoryMethods() {}

    @Around("repositoryMethods()")
    public Object measureMethodExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        // todo change to arg name

        LOGGER.info("before lock");
        ReentrantReadWriteLock objectLock = getObjectLock(pjp, ReentrantWriteLock.class);
        objectLock.readLock().lock();

        String methodName = pjp.getSignature().getName();
        LOGGER.info("before method: {} invocation", methodName);
        Object returnValue = pjp.proceed();
        LOGGER.info("after method: {} invocation", methodName);

        objectLock.readLock().unlock();
        LOGGER.info("after unlock");

        return returnValue;
    }
}
