package com.example.springaopreentrantreadwritelock;

import java.util.HashMap;
import java.util.Map;
import com.example.springaopreentrantreadwritelock.annotation.ReentrantReadWriteLock;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@ReentrantReadWriteLock
@ToString
public class Repository {

    private final Map<String, String> map = new HashMap<>();

    @ReentrantLockTransaction(readOnly = true)
    public String getValue(String key) {
        return map.get(key);
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
