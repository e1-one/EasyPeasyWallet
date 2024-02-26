package com.easypeasy.calculator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IntCalculatorTest {

    @Test
    public void testAddition() {
        IntCalculator intCalculator = new IntCalculator();
        assertEquals(5, intCalculator.add(2, 3));
    }

    @Test
    public void testIsMultiple() {
        IntCalculator intCalculator = new IntCalculator();
        assertTrue(intCalculator.isMultiple(15, 3));
    }

//    @Test
//    public void testDivisionByZero() {
//        IntCalculator intCalculator = new IntCalculator();
//        assertThrows(IllegalArgumentException.class, () -> intCalculator.divide(10, 0));
//    }

    @Test
    public void testIsPositive() {
        IntCalculator intCalculator = new IntCalculator();
        assertTrue(intCalculator.isPositive(5));
//        assertFalse(calculator.isPositive(-2));
//        assertFalse(calculator.isPositive(0));
    }
}