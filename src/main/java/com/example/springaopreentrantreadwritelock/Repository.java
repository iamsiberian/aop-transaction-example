package com.example.springaopreentrantreadwritelock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.springframework.stereotype.Component;

@Component
public class Repository {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, String> map = new HashMap<>();

    @ReentrantReadLock
    public String getValue(String key) {
        return map.get(key);
    }

    @ReentrantWriteLock
    public void setValue(String key, String value) {
        map.put(key, value);
    }

    @ReentrantWriteLock
    public void clearValues() {
        map.clear();
    }
}
