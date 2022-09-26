package com.example.springaopreentrantreadwritelock;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

public class ReentrantReadWriteLockRepositoryBeanPostProcessorTest {

    private final ReentrantReadWriteLockBeanPostProcessor postProcessor = new ReentrantReadWriteLockBeanPostProcessor();

    @Test
    void testByteBuddyCreateLockField() throws IllegalAccessException, NoSuchFieldException {

        Class<? extends Repository> loaded = postProcessor.createTargetClassWithReentrantLock(
                Repository.class
        );

        Object targetObj = postProcessor.createTargetObject(loaded);

        Field lockField = loaded.getDeclaredField("lock");
        lockField.setAccessible(true);

        assertNotNull(lockField.get(targetObj));
    }
}
