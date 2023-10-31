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

    @Autowired
    public FactorizationService(FactorizationRepository factorizationRepository) {
        this.factorizationRepository = factorizationRepository;
        this.numberFactorizor = new NumberFactorizor();
    }

    public Optional<FactorizationResult> getFactorizationResultFor(int number) {
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
        ArrayList<Integer> factors = numberFactorizor.factorize(number);
        return new FactorizationResult(number, factors.toString());
    }

    protected void saveFactorizationResult(FactorizationResult factorizationResult) {
        factorizationRepository.save(factorizationResult);
    }
}
