package com.example.springaopreentrantreadwritelock;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ReentrantLock {
    String value() default "lock";
}
