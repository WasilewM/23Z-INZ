package com.mwasilew.server_app.unit_tests.services_tests;

import com.mwasilew.server_app.models.FactorizationResult;
import com.mwasilew.server_app.repositories.FactorizationRepository;
import com.mwasilew.server_app.services.FactorizationService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FactorizationServiceWithMethodsCallingCounters extends FactorizationService {
    public int handleRequestCalculationsMethodCalls;
    public int calculateFactorizationResultMethodCalls;
    public int saveFactorizationResultMethodCalls;

    public FactorizationServiceWithMethodsCallingCounters(FactorizationRepository factorizationRepository) {
        super(factorizationRepository);
        handleRequestCalculationsMethodCalls = 0;
        calculateFactorizationResultMethodCalls = 0;
        saveFactorizationResultMethodCalls = 0;
    }

    public void resetMethodCallsCounters() {
        handleRequestCalculationsMethodCalls = 0;
        calculateFactorizationResultMethodCalls = 0;
        saveFactorizationResultMethodCalls = 0;
    }

    @Override
    protected Optional<FactorizationResult> handleRequestCalculations(int number) {
        handleRequestCalculationsMethodCalls += 1;
        return super.handleRequestCalculations(number);
    }

    @Override
    protected FactorizationResult calculateFactorizationResult(int number) {
        calculateFactorizationResultMethodCalls += 1;
        return super.calculateFactorizationResult(number);
    }

    @Override
    protected void saveFactorizationResult(FactorizationResult factorizationResult) {
        saveFactorizationResultMethodCalls += 1;
        super.saveFactorizationResult(factorizationResult);
    }
}
