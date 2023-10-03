package number_factorizator_tests;

import org.junit.Test;
import org.number_factorizator.NumberFactorizor;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class NumberFactorizorTest
{
    @Test
    public void givenFactorizeMethod_whenCalledWithZero_thenEmptyListIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        long inputNumber = 0L;
        ArrayList<Long> expectedResult = new ArrayList<>();
        assertEquals(expectedResult, nf.factorize(inputNumber));
    }

    @Test
    public void givenFactorizeMethod_whenCalledWithNegativeNumber_thenEmptyListIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        long inputNumber = -1L;
        ArrayList<Long> expectedResult = new ArrayList<>();
        assertEquals(expectedResult, nf.factorize(inputNumber));
    }

    @Test
    public void givenFactorizeMethod_whenCalledWithOne_thenListWithOneIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        long inputNumber = 1L;
        ArrayList<Long> expectedResult = new ArrayList<>();
        expectedResult.add(inputNumber);

        assertEquals(expectedResult, nf.factorize(inputNumber));
    }

    @Test
    public void givenFactorizeMethod_whenCalledWithPrimeNumber_thenListWithOneAndThisPrimeIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        long inputNumber = 61L;
        ArrayList<Long> expectedResult = new ArrayList<>();
        expectedResult.add(1L);
        expectedResult.add(inputNumber);

        assertEquals(expectedResult, nf.factorize(inputNumber));
    }

    @Test
    public void givenFactorizeMethod_whenCalledWithSix_thenListWithOneTwoAndThreeIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        long inputNumber = 6L;
        ArrayList<Long> expectedResult = new ArrayList<>();
        expectedResult.add(1L);
        expectedResult.add(2L);
        expectedResult.add(3L);

        assertEquals(expectedResult, nf.factorize(inputNumber));
    }

    @Test
    public void givenFactorizeMethod_whenCalledWithSixteen_thenListWithValidNumberOfPrimesIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        long inputNumber = 16L;
        ArrayList<Long> expectedResult = new ArrayList<>();
        expectedResult.add(1L);
        expectedResult.add(2L);
        expectedResult.add(2L);
        expectedResult.add(2L);
        expectedResult.add(2L);

        assertEquals(expectedResult, nf.factorize(inputNumber));
    }

    @Test
    public void givenIsPrimeMethod_whenCalledWithNegativeNumber_thenFalseIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        int inputNumber = -1;
        assertFalse(nf.isPrime(inputNumber));
    }

    @Test
    public void givenIsPrimeMethod_whenCalledWithZero_thenFalseIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        int inputNumber = 0;
        assertFalse(nf.isPrime(inputNumber));
    }

    @Test
    public void givenIsPrimeMethod_whenCalledWithOne_thenFalseIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        int inputNumber = 1;
        assertFalse(nf.isPrime(inputNumber));
    }

    @Test
    public void givenIsPrimeMethod_whenCalledWithSmallestPrime_thenTrueIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        int inputNumber = 2;
        assertTrue(nf.isPrime(inputNumber));
    }

    @Test
    public void givenIsPrimeMethod_whenCalledWithBigPrime_thenTrueIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        int inputNumber = 648391;
        assertTrue(nf.isPrime(inputNumber));
    }

    @Test
    public void givenIsPrimeMethod_whenCalledWithSmallPrime_thenTrueIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        int inputNumber = 7;
        assertTrue(nf.isPrime(inputNumber));
    }

    @Test
    public void givenIsPrimeMethod_whenCalledWithCompositeEvenNumber_thenFalseIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        int inputNumber = 22;
        assertFalse(nf.isPrime(inputNumber));
    }

    @Test
    public void givenIsPrimeMethod_whenCalledWithCompositeOddNumber_thenFalseIsReturned() {
        NumberFactorizor nf = new NumberFactorizor();
        int inputNumber = 27;
        assertFalse(nf.isPrime(inputNumber));
    }
}
