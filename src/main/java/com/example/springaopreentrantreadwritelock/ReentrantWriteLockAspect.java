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

        LOGGER.debug("before lock");
        ReentrantReadWriteLock objectLock = getObjectLock(pjp);
        objectLock.readLock().lock();

        String methodName = pjp.getSignature().getName();
        LOGGER.debug("before method: {} invocation", methodName);
        Object returnValue = pjp.proceed();
        LOGGER.debug("after method: {} invocation", methodName);

        objectLock.readLock().unlock();
        LOGGER.debug("after unlock");

        return returnValue;
    }
}
