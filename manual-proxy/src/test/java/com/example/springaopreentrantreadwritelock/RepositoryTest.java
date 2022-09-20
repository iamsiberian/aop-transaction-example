package com.example.springaopreentrantreadwritelock;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testGetValue() {
        String actualValue = repository.getValue("123");
        assertEquals("456", actualValue);
    }
}
