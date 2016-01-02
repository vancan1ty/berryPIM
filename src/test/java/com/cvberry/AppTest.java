package com.cvberry;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

/**
 * I usually primarily test the business logic layer.
 */
public class AppTest {

	@Test
    public void testNextPrime()
    {
        assertEquals(Model.findNextBiggerPrime(3),5);
        assertEquals(Model.findNextBiggerPrime(1),2);
        assertEquals(Model.findNextBiggerPrime(960),967);
    }
}
