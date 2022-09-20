package com.example.springaopreentrantreadwritelock;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;
import org.junit.jupiter.api.Test;

public class ReentrantReadWriteLockRepositoryBeanPostProcessorTest {

    private final ByteBuddy byteBuddy = new ByteBuddy();

    @Test
    void testByteBuddyCreateLockField() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Class<?> loaded =  byteBuddy
                .subclass(Repository.class)
                .name("com.example.springaopreentrantreadwritelock.RepositoryWithReentrantLock")
                .defineField("lock", java.util.concurrent.locks.ReentrantReadWriteLock.class, Modifier.PRIVATE | Modifier.FINAL)
                .defineConstructor(Visibility.PUBLIC)
                .withParameters(ReentrantReadWriteLock.class)
                .intercept(
                        MethodCall
                                .invoke(Repository.class.getConstructor())
                                .andThen(FieldAccessor.ofField("lock").setsArgumentAt(0))
                )
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();

        Object targetObj = loaded.getConstructor(ReentrantReadWriteLock.class).newInstance(new ReentrantReadWriteLock());

        Field lockField = loaded.getDeclaredField("lock");
        lockField.setAccessible(true);

        assertNotNull(lockField.get(targetObj));
    }
}
