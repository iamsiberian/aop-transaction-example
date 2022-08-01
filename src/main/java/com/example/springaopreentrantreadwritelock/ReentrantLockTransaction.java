package com.example.springaopreentrantreadwritelock;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ReentrantLockTransaction {
    String value() default "lock";
    boolean readOnly() default false;
}
