package com.example.springaopreentrantreadwritelock;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

@Slf4j
public class ReentrantLockTransactionAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        ReentrantReadWriteLock objectLock = getObjectLock(invocation);

        ReentrantLockTransactionRepository reentrantLockTransactionAnnotation = getReentrantLockTransactionAnnotation(invocation);

        String methodName = invocation.getMethod().getName();
        if (reentrantLockTransactionAnnotation.readOnly()) {
            LOGGER.info("before read lock");
            objectLock.readLock().lock();

            LOGGER.info("before method: {} invocation", methodName);
            Object returnValue = invocation.proceed();
            LOGGER.info("after method: {} invocation", methodName);

            objectLock.readLock().unlock();
            LOGGER.info("after read unlock");

            return returnValue;
        } else {
            LOGGER.info("before write lock");
            objectLock.writeLock().lock();

            LOGGER.info("before method: {} invocation", methodName);
            Object returnValue = invocation.proceed();
            LOGGER.info("after method: {} invocation", methodName);

            objectLock.writeLock().unlock();
            LOGGER.info("after write unlock");

            return returnValue;
        }
    }

    private ReentrantReadWriteLock getObjectLock(
            MethodInvocation invocation
    ) {

        ReentrantLockTransactionRepository reentrantLockTransactionAnnotation = getReentrantLockTransactionAnnotation(invocation);
        String reentrantLockName = reentrantLockTransactionAnnotation.value();

        Object targetObject = invocation.getThis();

        if (targetObject == null) {
            targetObject = invocation.getStaticPart();
        }

        Class<?> targetClass = targetObject.getClass();
        Field lockField;
        try {
            lockField = targetClass.getDeclaredField(reentrantLockName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("""
            Repository: %s does not have ReentrantReadWriteLock field: %s
            please set default name <%s> or set your
        """.formatted(targetObject, reentrantLockName, "lock"));
        }
        lockField.setAccessible(true);

        try {
            return (ReentrantReadWriteLock) lockField.get(targetObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private ReentrantLockTransactionRepository getReentrantLockTransactionAnnotation(
            MethodInvocation invocation
    ) {
        Method method = invocation.getMethod();
        return method.getAnnotation(ReentrantLockTransactionRepository.class);
    }
}
