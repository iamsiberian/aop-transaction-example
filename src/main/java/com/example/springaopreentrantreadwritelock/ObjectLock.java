package com.example.springaopreentrantreadwritelock;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.aspectj.lang.ProceedingJoinPoint;

public interface ObjectLock {

    default ReentrantReadWriteLock getObjectLock(ProceedingJoinPoint pjp) {
        Object targetObject = pjp.getTarget();
        Field[] targetObjectFields = targetObject.getClass().getFields();
        List<ReentrantReadWriteLock> reentrantReadWriteLocks = Arrays
                .stream(targetObjectFields)
                .filter(targetObjectField -> targetObjectField.getType() == ReentrantReadWriteLock.class)
                .map(targetObjectField -> {
                    try {
                        return (ReentrantReadWriteLock) targetObjectField.get(this);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("error while getting ReentrantReadWriteLock object from field: " + targetObjectField);
                    }
                })
                .toList();

        if (reentrantReadWriteLocks.size() == 0) {
            throw new RuntimeException(targetObject + " has none ReentrantReadWriteLock");
        }

        if (reentrantReadWriteLocks.size() > 1) {
            throw new RuntimeException(targetObject + " has more 1 ReentrantReadWriteLock");
        }

        return reentrantReadWriteLocks.get(0);
    }
}
