package com.mwasilew.server_app.controllers;

import com.mwasilew.server_app.NumberFactorizor;
import com.mwasilew.server_app.models.FactorizationResult;
import com.mwasilew.server_app.services.FactorizationResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping(path="factorization_results")
public class FactorizationResultController {
    private final FactorizationResultService factorizationResultService;

    @Autowired
    public FactorizationResultController(FactorizationResultService factorizationResultService) {
        this.factorizationResultService = factorizationResultService;
    }


    @GetMapping("{number}")
    public Optional<FactorizationResult> getFactorizationResult(@PathVariable int number) {
        long calcStartTime = System.currentTimeMillis();
        Optional<FactorizationResult> cacheResult = factorizationResultService.getFactorizationResultFor(number);
        if (cacheResult.isEmpty()) {
            return handleRequestCalculations(number, calcStartTime);
        }
        return handleRequestsWithoutCalculations(number, calcStartTime, cacheResult);
    }

    private Optional<FactorizationResult> handleRequestCalculations(int number, long calcStartTime) {
        synchronized (FactorizationResultController.class) {
            Optional<FactorizationResult> cacheResult = factorizationResultService.getFactorizationResultFor(number);
            if (cacheResult.isEmpty()) {
                FactorizationResult factorizationResult = calculateFactorizationResult(number);
                factorizationResultService.saveFactorizationResult(factorizationResult);
                long calcEndTime = System.currentTimeMillis();
                long calcTime = calcEndTime - calcStartTime;
                System.out.println("Not found in cache: " + number + " - took: " + calcTime);
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
        System.out.println("Cache hit for: " + number + " - took: " + calcTime);
        return cacheResult;
    }
}
