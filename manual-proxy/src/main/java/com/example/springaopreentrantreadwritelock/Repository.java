package com.example.springaopreentrantreadwritelock;

import java.util.HashMap;
import java.util.Map;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@ReentrantReadWriteLockRepository
@ToString
public class Repository {

    private final Map<String, String> map = new HashMap<>();

    @ReentrantLockTransactionRepository(readOnly = true)
    public String getValue(String key) {
        return map.get(key);
    }

    @ReentrantLockTransactionRepository
    public void setValue(String key, String value) {
        map.put(key, value);
    }

    @ReentrantLockTransactionRepository
    public void clearValues() {
        map.clear();
    }
}
