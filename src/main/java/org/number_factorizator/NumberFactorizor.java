package org.number_factorizator;

import java.util.ArrayList;

public class NumberFactorizor {
    private final ArrayList<Integer> factorizationSolution;

    public NumberFactorizor() {
        this.factorizationSolution = new ArrayList<>();
    }

    public ArrayList<Integer> factorize(int inputNumber) {
        if (inputNumber >= 1) {
            factorizationSolution.add(1);
        } else {
            return factorizationSolution;
        }

        addAllDivisorsOf(inputNumber, 2);
        for (int i = 3; i <= inputNumber; i += 2) {
            if(isPrime(i)) {
                addAllDivisorsOf(inputNumber, i);
            }
        }
        return factorizationSolution;
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

    private static boolean isCompositeEvenNumber(int inputNumber) {
        return inputNumber > 2 && inputNumber % 2 == 0;
    }
}
