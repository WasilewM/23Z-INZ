package com.mwasilew.server_app.services;

import com.mwasilew.server_app.models.FactorizationResult;
import com.mwasilew.server_app.repositories.FactorizationRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FactorizationLoggerService extends FactorizationService {
    private final Timer factorizationTimer;
    private final Counter calculationsCounter;

    @Autowired
    public FactorizationLoggerService(FactorizationRepository factorizationRepository, CompositeMeterRegistry mr) {
        super(factorizationRepository);
        this.factorizationTimer = Timer.builder("factorization_timer").register(mr);
        this.calculationsCounter = Counter.builder("calculations_counter").register(mr);
    }

    @Override
    public Optional<FactorizationResult> getFactorizationResultFor(int number) {
        long calcStartTime = System.currentTimeMillis();
        Optional<FactorizationResult> result = super.getFactorizationResultFor(number);
        long calcEndTime = System.currentTimeMillis();
        factorizationTimer.record(calcEndTime - calcStartTime, TimeUnit.MILLISECONDS);
        return result;
    }

    @Override
    protected FactorizationResult calculateFactorizationResult(int number) {
        log.info("Not found in cache: " + number);
        calculationsCounter.increment();
        return super.calculateFactorizationResult(number);
    }

    @Override
    protected FactorizationResult getObjectForIllegalArgumentException(int number, Exception e) {
        log.error(e.getMessage());
        return super.getObjectForIllegalArgumentException(number, e);
    }
}
