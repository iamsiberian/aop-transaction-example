package com.example.springaopreentrantreadwritelock;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public interface ObjectLock {

    default ReentrantReadWriteLock getObjectLock(
            ProceedingJoinPoint pjp
    ) {

        ReentrantLockTransaction reentrantLockTransactionAnnotation = getReentrantLockTransactionAnnotation(pjp);
        String reentrantLockName = reentrantLockTransactionAnnotation.value();

        Object targetObject =  pjp.getTarget();
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

    default ReentrantLockTransaction getReentrantLockTransactionAnnotation(
            ProceedingJoinPoint pjp
    ) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(ReentrantLockTransaction.class);
    }
}
