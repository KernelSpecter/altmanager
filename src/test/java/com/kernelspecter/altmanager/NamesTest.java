package com.kernelspecter.altmanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import org.junit.Test;

public class NamesTest {
    @Test
    public void followsMinecraftsRules() {
        assertTrue(Names.valid("abc"));
        assertTrue(Names.valid("Some_Name_123456"));
        assertFalse(Names.valid("ab"));
        assertFalse(Names.valid("Some_Name_1234567"));
        assertFalse(Names.valid("bad name"));
        assertFalse(Names.valid("bad-name"));
        assertFalse(Names.valid("näme"));
        assertFalse(Names.valid(""));
        assertFalse(Names.valid(null));
    }

    @Test
    public void randomNamesAreAlwaysValid() {
        Random r = new Random(42);
        for (int i = 0; i < 5000; i++) {
            String n = Names.random(r);
            assertTrue(n, Names.valid(n));
        }
    }
}
