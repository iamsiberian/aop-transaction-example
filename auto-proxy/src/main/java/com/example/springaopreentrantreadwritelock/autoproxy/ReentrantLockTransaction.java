package com.example.springaopreentrantreadwritelock.autoproxy;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ReentrantLockTransaction {
    String value() default "lock";
    boolean readOnly() default false;
}
