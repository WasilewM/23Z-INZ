package org.number_factorizator;

import java.util.ArrayList;

public class NumberFactorizor {
    public ArrayList<Long> factorize(long inputNumber) {
        ArrayList<Long> result = new ArrayList<>();
        if (inputNumber >= 1L) {
            result.add(1L);
        }
        for (long i = 2; i <= inputNumber; i += 1) {
            if(isPrime(i)) {
                long currentValue = inputNumber;
                while (currentValue % i == 0) {
                    result.add(i);
                    currentValue /= i;
                }
            }
        }
        return result;
    }

    public boolean isPrime(long inputNumber) {
        if (inputNumber < 2) {
            return false;
        }

        if (isCompositeEvenNumber(inputNumber)) {
            return false;
        }

        for (long i = 3; i < Math.sqrt(inputNumber); i += 2) {
            if (inputNumber % i == 0) {
                return false;
            }
        }

        return true;
    }

    private static boolean isCompositeEvenNumber(long inputNumber) {
        return inputNumber > 2 && inputNumber % 2 == 0;
    }
}
