package com.example.springaopreentrantreadwritelock;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
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
public class ReentrantReadLockAspect implements ObjectLock {

    @Pointcut("@target(com.example.springaopreentrantreadwritelock.ReentrantReadLock)")
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
