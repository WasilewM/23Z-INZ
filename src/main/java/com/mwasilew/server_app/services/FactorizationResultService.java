package com.mwasilew.server_app.services;

import com.mwasilew.server_app.NumberFactorizor;
import com.mwasilew.server_app.controllers.FactorizationResultController;
import com.mwasilew.server_app.models.FactorizationResult;
import com.mwasilew.server_app.repositories.FactorizationResultRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class FactorizationResultService {
    private final FactorizationResultRepository factorizationResultRepository;

    @Autowired
    public FactorizationResultService(FactorizationResultRepository factorizationResultRepository) {
        this.factorizationResultRepository = factorizationResultRepository;
    }

    public Optional<FactorizationResult> getFactorizationResultFor(int number) {
        long calcStartTime = System.currentTimeMillis();
        Optional<FactorizationResult> cacheResult = factorizationResultRepository.findById(number);
        if (cacheResult.isEmpty()) {
            return handleRequestCalculations(number, calcStartTime);
        }
        return handleRequestsWithoutCalculations(number, calcStartTime, cacheResult);
    }

    public void saveFactorizationResult(FactorizationResult factorizationResult) {
        factorizationResultRepository.save(factorizationResult);
    }

    private Optional<FactorizationResult> handleRequestCalculations(int number, long calcStartTime) {
        synchronized (FactorizationResultController.class) {
            Optional<FactorizationResult> cacheResult = factorizationResultRepository.findById(number);
            if (cacheResult.isEmpty()) {
                FactorizationResult factorizationResult = calculateFactorizationResult(number);
                this.saveFactorizationResult(factorizationResult);
                long calcEndTime = System.currentTimeMillis();
                long calcTime = calcEndTime - calcStartTime;
                log.info("Not found in cache: " + number + " - took: " + calcTime);
                return Optional.of(factorizationResult);
            } else {
                return handleRequestsWithoutCalculations(number, calcStartTime, cacheResult);
            }
        }
    }

    private static FactorizationResult calculateFactorizationResult(int number) {
        NumberFactorizor nr = new NumberFactorizor();
        ArrayList<Integer> factors = nr.factorize(number);
        return new FactorizationResult(number, factors.toString());
    }

    private static Optional<FactorizationResult> handleRequestsWithoutCalculations(int number, long calcStartTime, Optional<FactorizationResult> cacheResult) {
        long calcEndTime = System.currentTimeMillis();
        long calcTime = calcEndTime - calcStartTime;
        log.info("Cache hit for: " + number + " - time taken: " + calcTime);
        return cacheResult;
    }
}
