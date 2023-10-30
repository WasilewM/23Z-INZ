package com.mwasilew.server_app.unit_tests.number_factorizor_tests;

import com.mwasilew.server_app.NumberFactorizor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class NumberFactorizorTest {
    @Test
    public void givenFactorizeMethod_whenCalledWithZero_thenEmptyListIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        int inputNumber = 0;
        ArrayList<Integer> expectedResult = new ArrayList<>();
        assertEquals(expectedResult, nf.factorize(inputNumber));
    }

    @Test
    public void givenFactorizeMethod_whenCalledWithNegativeNumber_thenIllegalArgumentExceptionIsThrown() {
        NumberFactorizor nf = new NumberFactorizor();
        int inputNumber = -1;
        boolean exceptionCaught = false;
        try {
            nf.factorize(inputNumber);
        } catch (IllegalArgumentException e) {
            exceptionCaught = true;
            String msg = "Expected number in range 0 - " + NumberFactorizor.FACTORIZATION_RANGE;
            msg += ", but received \"-1\"";
            assertEquals(msg, e.getMessage());
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void givenFactorizeMethod_whenCalledWithNumberExceedingTheRange_thenEmptyListIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        int aboveRange = NumberFactorizor.FACTORIZATION_RANGE + 1;
        boolean exceptionCaught = false;
        try {
            nf.factorize(aboveRange);
        } catch (IllegalArgumentException e) {
            exceptionCaught = true;
            String msg = "Expected number in range 0 - " + NumberFactorizor.FACTORIZATION_RANGE;
            msg += ", but received \"" + aboveRange + "\"";
            assertEquals(msg, e.getMessage());
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void givenFactorizeMethod_whenCalledWithOne_thenListWithOneIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        int inputNumber = 1;
        ArrayList<Integer> expectedResult = new ArrayList<>();
        expectedResult.add(inputNumber);

        assertEquals(expectedResult, nf.factorize(inputNumber));
    }

    @Test
    public void givenFactorizeMethod_whenCalledWithFirstPrimeNumber_thenListWithOneAndThisPrimeIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        int inputNumber = 2;
        ArrayList<Integer> expectedResult = new ArrayList<>();
        expectedResult.add(1);
        expectedResult.add(inputNumber);

        assertEquals(expectedResult, nf.factorize(inputNumber));
    }

    @Test
    public void givenFactorizeMethod_whenCalledWithPrimeNumber_thenListWithOneAndThisPrimeIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        int inputNumber = 61;
        ArrayList<Integer> expectedResult = new ArrayList<>();
        expectedResult.add(1);
        expectedResult.add(inputNumber);

        assertEquals(expectedResult, nf.factorize(inputNumber));
    }

    @Test
    public void givenFactorizeMethod_whenCalledWithSix_thenListWithOneTwoAndThreeIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        int inputNumber = 6;
        ArrayList<Integer> expectedResult = new ArrayList<>();
        expectedResult.add(1);
        expectedResult.add(2);
        expectedResult.add(3);

        assertEquals(expectedResult, nf.factorize(inputNumber));
    }

    @Test
    public void givenFactorizeMethod_whenCalledWithSixteen_thenListWithValidNumberOfPrimesIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        int inputNumber = 16;
        ArrayList<Integer> expectedResult = new ArrayList<>();
        expectedResult.add(1);
        expectedResult.add(2);
        expectedResult.add(2);
        expectedResult.add(2);
        expectedResult.add(2);

        assertEquals(expectedResult, nf.factorize(inputNumber));
    }

    @Test
    public void givenFactorizeMethod_whenCalledWithBigNumber_thenListWithValidNumberOfPrimesIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        int inputNumber = 2147483;
        ArrayList<Integer> expectedResult = new ArrayList<>();
        expectedResult.add(1);
        expectedResult.add(13);
        expectedResult.add(13);
        expectedResult.add(97);
        expectedResult.add(131);
        expectedResult.add(169);

        assertEquals(expectedResult, nf.factorize(inputNumber));
    }

    @Test
    public void givenFactorizeMethod_whenCalledMaxNumber_thenListWithValidNumberOfPrimesIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        int inputNumber = NumberFactorizor.FACTORIZATION_RANGE;
        ArrayList<Integer> expectedResult = new ArrayList<>();
        expectedResult.add(1);
        expectedResult.add(2);
        expectedResult.add(173);
        expectedResult.add(31033);

        assertEquals(expectedResult, nf.factorize(inputNumber));
    }

    @Test
    public void givenIsPrimeMethod_whenCalledWithNegativeNumber_thenFalseIsReturned() {
        int inputNumber = -1;
        assertFalse(NumberFactorizor.isPrime(inputNumber));
    }

    @Test
    public void givenIsPrimeMethod_whenCalledWithZero_thenFalseIsReturned() {
        int inputNumber = 0;
        assertFalse(NumberFactorizor.isPrime(inputNumber));
    }

    @Test
    public void givenIsPrimeMethod_whenCalledWithOne_thenFalseIsReturned() {
        int inputNumber = 1;
        assertFalse(NumberFactorizor.isPrime(inputNumber));
    }

    @Test
    public void givenIsPrimeMethod_whenCalledWithSmallestPrime_thenTrueIsReturned() {
        int inputNumber = 2;
        assertTrue(NumberFactorizor.isPrime(inputNumber));
    }

    @Test
    public void givenIsPrimeMethod_whenCalledWithBigPrime_thenTrueIsReturned() {
        int inputNumber = 648391;
        assertTrue(NumberFactorizor.isPrime(inputNumber));
    }

    @Test
    public void givenIsPrimeMethod_whenCalledWithSmallPrime_thenTrueIsReturned() {
        int inputNumber = 7;
        assertTrue(NumberFactorizor.isPrime(inputNumber));
    }

    @Test
    public void givenIsPrimeMethod_whenCalledWithCompositeEvenNumber_thenFalseIsReturned() {
        int inputNumber = 22;
        assertFalse(NumberFactorizor.isPrime(inputNumber));
    }

    @Test
    public void givenIsPrimeMethod_whenCalledWithCompositeOddNumber_thenFalseIsReturned() {
        int inputNumber = 27;
        assertFalse(NumberFactorizor.isPrime(inputNumber));
    }
}
