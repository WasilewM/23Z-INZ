package com.mwasilew.server_app;

import java.util.ArrayList;

public class NumberFactorizor {
    public static final int FACTORIZATION_RANGE = Integer.MAX_VALUE / 200;
    private final ArrayList<Integer> factorizationSolution;

    public NumberFactorizor() {
        this.factorizationSolution = new ArrayList<>();
    }

    public ArrayList<Integer> factorize(int inputNumber) throws IllegalArgumentException {
        if (!isNumberInRange(inputNumber)) {
            String msg = "Expected number in range 0 - " + FACTORIZATION_RANGE;
            msg += ", but received \"" + inputNumber + "\"";
            throw new IllegalArgumentException(msg);
        }

        addOneDivisorForPositiveNumber(inputNumber);
        addTwoDivisorForPositiveEvenNumber(inputNumber);
        for (int i = 3; i <= inputNumber; i += 2) {
            if(isPrime(i)) {
                addAllDivisorsOf(inputNumber, i);
            }
        }
        return factorizationSolution;
    }

    private void addTwoDivisorForPositiveEvenNumber(int inputNumber) {
        if (isPositiveEvenNumber(inputNumber)) {
            addAllDivisorsOf(inputNumber, 2);
        }
    }

    private static boolean isNumberInRange(int inputNumber) {
        return inputNumber >= 0 && inputNumber <= FACTORIZATION_RANGE;
    }

    private void addOneDivisorForPositiveNumber(int inputNumber) {
        if (inputNumber >= 1) {
            factorizationSolution.add(1);
        }
    }

    private void addAllDivisorsOf(int inputNumber, int divisor) {
        int currentValue = inputNumber;
        while (currentValue % divisor == 0) {
            factorizationSolution.add(divisor);
            currentValue /= divisor;
        }
    }

    public static boolean isPrime(int inputNumber) {
        if (inputNumber < 2) {
            return false;
        }

        if (isCompositeEvenNumber(inputNumber)) {
            return false;
        }

        for (int i = 3; i < Math.sqrt(inputNumber); i += 2) {
            if (inputNumber % i == 0) {
                return false;
            }
        }

        return true;
    }

    private static boolean isPositiveEvenNumber(int inputNumber) {
        return inputNumber > 2 && inputNumber % 2 == 0;
    }

    private static boolean isCompositeEvenNumber(int inputNumber) {
        return inputNumber > 2 && inputNumber % 2 == 0;
    }
}
