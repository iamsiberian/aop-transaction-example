package com.example.springaopreentrantreadwritelock.autoproxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RepositoryTest {

    @Autowired
    private Repository repository;

    @BeforeEach
    void setUp() {
        repository.clearValues();
        repository.setValue("123", "456");
    }

    @Test
    void testGetValueWithCheckDeadlock() {
        RuntimeException runtimeException = assertThrows(
                RuntimeException.class,
                () -> repository.getValue("abrakadabra")
        );
        assertEquals("Value by key: abrakadabra is null", runtimeException.getMessage());

        String actualValue = repository.getValue("123");
        assertEquals("456", actualValue);
    }
}
