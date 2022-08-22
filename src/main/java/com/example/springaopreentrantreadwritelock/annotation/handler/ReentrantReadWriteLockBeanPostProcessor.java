package com.example.springaopreentrantreadwritelock.annotation.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import com.example.springaopreentrantreadwritelock.Repository;
import com.example.springaopreentrantreadwritelock.annotation.ReentrantReadWriteLock;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;
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
        if (checkReentrantLockAnnotation(bean)) {
            try {
                Class<?> loaded =  byteBuddy
                        .subclass(Repository.class)
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
                                        .invoke(Repository.class.getConstructor())
                                        .andThen(FieldAccessor.ofField("lock").setsArgumentAt(0))
                        )
                        .make()
                        .load(getClass().getClassLoader())
                        .getLoaded();

                return loaded
                        .getConstructor(java.util.concurrent.locks.ReentrantReadWriteLock.class)
                        .newInstance(new java.util.concurrent.locks.ReentrantReadWriteLock());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return bean;
    }

    boolean checkReentrantLockAnnotation(Object bean) {
        ReentrantReadWriteLock reentrantReadWriteLockAnnotation = AnnotationUtils.getAnnotation(
                ClassUtils.getUserClass(bean.getClass()),
                ReentrantReadWriteLock.class
        );
        return reentrantReadWriteLockAnnotation != null;
    }
}
