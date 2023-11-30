package com.mwasilew.server_app.services;

import com.mwasilew.server_app.NumberFactorizor;
import com.mwasilew.server_app.controllers.FactorizationController;
import com.mwasilew.server_app.models.FactorizationResult;
import com.mwasilew.server_app.repositories.FactorizationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;


@Service
@Transactional
public class FactorizationService {
    private final FactorizationRepository factorizationRepository;
    private final NumberFactorizor numberFactorizor;
    public final String resultForInvalidRequest;

    @Autowired
    public FactorizationService(FactorizationRepository factorizationRepository) {
        this.factorizationRepository = factorizationRepository;
        this.numberFactorizor = new NumberFactorizor();
        this.resultForInvalidRequest = "";
    }

    public Optional<FactorizationResult> getFactorizationResultFor(int number) {
        try {
            return tryToGetFactorizationResultFor(number);
        } catch (Exception e) {
            return Optional.of(getObjectWhenExceptionOccurred(number, e));
        }
    }

    protected Optional<FactorizationResult> tryToGetFactorizationResultFor(int number) {
        Optional<FactorizationResult> cacheResult = factorizationRepository.findById(number);
        if (cacheResult.isEmpty()) {
            return handleRequestCalculations(number);
        }
        return cacheResult;
    }

    protected Optional<FactorizationResult> handleRequestCalculations(int number) {
        synchronized (FactorizationController.class) {
            Optional<FactorizationResult> cacheResult = factorizationRepository.findById(number);
            if (cacheResult.isEmpty()) {
                FactorizationResult factorizationResult = calculateFactorizationResult(number);
                saveFactorizationResult(factorizationResult);
                return Optional.of(factorizationResult);
            } else {
                return cacheResult;
            }
        }
    }

    protected FactorizationResult calculateFactorizationResult(int number) {
        try {
            ArrayList<Integer> factors = numberFactorizor.factorize(number);
            return new FactorizationResult(number, factors.toString());
        } catch (IllegalArgumentException e) {
            return getObjectWhenExceptionOccurred(number, e);
        }
    }

    protected FactorizationResult getObjectWhenExceptionOccurred(int number, Exception e) {
        return new FactorizationResult(number, resultForInvalidRequest);
    }

    protected void saveFactorizationResult(FactorizationResult factorizationResult) {
        if (!factorizationResult.getFactors().equals(resultForInvalidRequest)) {
            factorizationRepository.save(factorizationResult);
        }
    }
}
