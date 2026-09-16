    package com.company.automation;

    import org.junit.jupiter.api.Test;

import com.example.App;

import static org.junit.jupiter.api.Assertions.*;

    public class AppTest {
        @Test
        public void testAdd() {
            assertEquals(5, App.add(2, 3));
        }
    }
