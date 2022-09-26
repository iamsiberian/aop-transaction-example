package com.example.springaopreentrantreadwritelock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.ClassUtils;

@Configuration
@Order
public class ReentrantReadWriteLockBeanPostProcessor implements BeanPostProcessor {

    private final ByteBuddy byteBuddy = new ByteBuddy();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> targetClass = ClassUtils.getUserClass(bean.getClass());
        if (checkReentrantLockAnnotation(targetClass)) {
            Object targetObject = createTargetObject(createTargetClassWithReentrantLock(targetClass));
            return createProxyTargetObject(targetObject);
        }

        return bean;
    }

    <T> Class<? extends T> createTargetClassWithReentrantLock(Class<T> targetClass) {
        try {
            return byteBuddy
                    .subclass(targetClass)
                    .name("com.example.springaopreentrantreadwritelock.RepositoryWithReentrantLock")
                    .defineField(
                            "lock",
                            java.util.concurrent.locks.ReentrantReadWriteLock.class,
                            Modifier.PRIVATE | Modifier.FINAL
                    )
                    .defineConstructor(Visibility.PUBLIC)
                    .withParameters(java.util.concurrent.locks.ReentrantReadWriteLock.class)
                    .intercept(
                            MethodCall
                                    .invoke(targetClass.getConstructor())
                                    .andThen(FieldAccessor.ofField("lock").setsArgumentAt(0))
                    )
                    .make()
                    .load(getClass().getClassLoader())
                    .getLoaded();

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    <T> T createTargetObject(Class<? extends T> targetClassWithReentrantLock) {
        try {
            return targetClassWithReentrantLock
                    .getConstructor(java.util.concurrent.locks.ReentrantReadWriteLock.class)
                    .newInstance(new java.util.concurrent.locks.ReentrantReadWriteLock());
        } catch (
                NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e
        ) {
            throw new RuntimeException(e);
        }
    }

    Object createProxyTargetObject(Object targetObject) {
        Advisor reentrantLockTransactionAdvisor = createReentrantLockAdvisor();

        return createAopProxyWithAdvisor(targetObject, reentrantLockTransactionAdvisor);
    }

    boolean checkReentrantLockAnnotation(Class<?> targetClass) {
        ReentrantReadWriteLockRepository reentrantReadWriteLockRepositoryAnnotation = AnnotationUtils.getAnnotation(
                targetClass,
                ReentrantReadWriteLockRepository.class
        );
        return reentrantReadWriteLockRepositoryAnnotation != null;
    }

    Advisor createReentrantLockAdvisor() {
        Pointcut reentrantLockTransactionPointCut = new ReentrantLockTransactionPointCut();
        Advice reentrantLockTransactionAdvice = new ReentrantLockTransactionAdvice();
        return new DefaultPointcutAdvisor(
                reentrantLockTransactionPointCut, reentrantLockTransactionAdvice
        );
    }

    Object createAopProxyWithAdvisor(Object targetObject, Advisor advisor) {
        ProxyFactory pf = new ProxyFactory();
        pf.setTarget(targetObject);
        pf.addAdvisor(advisor);

        return pf.getProxy();
    }
}
