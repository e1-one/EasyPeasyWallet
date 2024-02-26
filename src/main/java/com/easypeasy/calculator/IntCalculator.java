package com.easypeasy.calculator;

import java.math.BigInteger;

public class IntCalculator {

    public int add(int a, int b) {
        return a + b;
    }

    public boolean isMultiple(int dividend, int divisor) {
        return dividend % divisor == 0;
    }

    public boolean isPositive(int number) {
        return number > 0;
    }

    public BigInteger increment(BigInteger bi){
        bi.add(BigInteger.ONE);
        return bi;
    }
}
