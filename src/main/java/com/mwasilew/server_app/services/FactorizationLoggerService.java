package com.mwasilew.server_app.services;

import com.mwasilew.server_app.models.FactorizationResult;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class FactorizationLoggerService {
    private final FactorizationService factorizationService;
    private final Timer factorizationTimer;

    @Autowired
    public FactorizationLoggerService(FactorizationService fs, CompositeMeterRegistry mr) {
        this.factorizationService = fs;
        this.factorizationTimer = Timer.builder("factorization_timer").register(mr);
    }

    public Optional<FactorizationResult> getFactorizationResultFor(int number) {
        long calcStartTime = System.currentTimeMillis();
        Optional<FactorizationResult> result = factorizationService.getFactorizationResultFor(number);
        long calcEndTime = System.currentTimeMillis();
        factorizationTimer.record(calcEndTime - calcStartTime, TimeUnit.MILLISECONDS);
        return result;
    }
}
