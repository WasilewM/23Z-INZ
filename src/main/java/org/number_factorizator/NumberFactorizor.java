package org.number_factorizator;

import java.util.ArrayList;

public class NumberFactorizor {
    public ArrayList<Integer> factorize(int inputNumber) {
        ArrayList<Integer> result = new ArrayList<>();
        if (inputNumber >= 1) {
            result.add(1);
        }
        for (int i = 2; i <= inputNumber; i += 1) {
            if(isPrime(i)) {
                int currentValue = inputNumber;
                while (currentValue % i == 0) {
                    result.add(i);
                    currentValue /= i;
                }
            }
        }
        return result;
    }

    public boolean isPrime(int inputNumber) {
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
