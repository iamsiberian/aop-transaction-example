package com.example.springaopreentrantreadwritelock.autoproxy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@ToString
public class Repository {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, String> map = new HashMap<>();

    @ReentrantLockTransaction(readOnly = true)
    public String getValue(String key) {
        String value = map.get(key);

        if (value == null) {
            throw new RuntimeException("""
            Value by key: %s is null
        """.formatted(key));
        }

        return value;
    }

    @ReentrantLockTransaction
    public void setValue(String key, String value) {
        map.put(key, value);
    }

    @ReentrantLockTransaction
    public void clearValues() {
        map.clear();
    }
}
