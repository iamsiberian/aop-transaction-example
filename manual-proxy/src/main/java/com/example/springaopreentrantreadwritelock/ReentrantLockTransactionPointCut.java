package com.example.springaopreentrantreadwritelock;

import java.lang.reflect.Method;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

public class ReentrantLockTransactionPointCut extends StaticMethodMatcherPointcut {

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return method.getDeclaredAnnotation(ReentrantLockTransactionRepository.class) != null;
    }
}
