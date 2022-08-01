package com.example.springaopreentrantreadwritelock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public interface ObjectLock {

    default <T extends Annotation> ReentrantReadWriteLock getObjectLock(
            ProceedingJoinPoint pjp, Class<T> annotationClass
    ) {

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        T reentrantReadOrWriteLockAnnotation = method.getAnnotation(annotationClass);
        ReentrantLock reentrantLockAnnotation = reentrantReadOrWriteLockAnnotation
                .annotationType()
                .getAnnotation(ReentrantLock.class);
        String reentrantLockName = reentrantLockAnnotation.value();

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
}
