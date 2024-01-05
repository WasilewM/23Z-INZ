package com.mwasilew.server_app;

import java.util.ArrayList;

public class NumberFactorizor {
    public static final int FACTORIZATION_RANGE = Integer.MAX_VALUE / 200;
    private final ArrayList<Integer> factorizationSolution;

    public NumberFactorizor() {
        this.factorizationSolution = new ArrayList<>();
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

    private static boolean isNumberInRange(int inputNumber) {
        return inputNumber >= 0 && inputNumber <= FACTORIZATION_RANGE;
    }

    private static boolean isCompositeEvenNumber(int inputNumber) {
        return inputNumber > 2 && inputNumber % 2 == 0;
    }

    public ArrayList<Integer> factorize(int inputNumber) throws IllegalArgumentException {
        clearPreviousSolution();
        if (!isNumberInRange(inputNumber)) {
            String msg = "Expected number in range 0 - " + FACTORIZATION_RANGE;
            msg += ", but received \"" + inputNumber + "\"";
            throw new IllegalArgumentException(msg);
        }

        if (inputNumber < 2) {
            return factorizationSolution;
        }

        while (inputNumber % 2 == 0) {
            factorizationSolution.add(2);
            inputNumber /= 2;
        }

        for (int i = 3; i <= inputNumber; i += 2) {
            if (isPrime(i)) {
                while (inputNumber % i == 0) {
                    factorizationSolution.add(i);
                    inputNumber /= i;
                }
            }
        }
        return factorizationSolution;
    }

    private void clearPreviousSolution() {
        factorizationSolution.clear();
    }
}
