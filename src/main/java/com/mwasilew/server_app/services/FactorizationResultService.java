package com.mwasilew.server_app.services;

import com.mwasilew.server_app.models.FactorizationResult;
import com.mwasilew.server_app.repositories.FactorizationResultRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class FactorizationResultService {
    private final FactorizationResultRepository factorizationResultRepository;

    @Autowired
    public FactorizationResultService(FactorizationResultRepository factorizationResultRepository) {
        this.factorizationResultRepository = factorizationResultRepository;
    }

    public Optional<FactorizationResult> getFactorizationResultFor(int number) {
        return factorizationResultRepository.findById(number);
    }

    public void saveFactorizationResult(FactorizationResult factorizationResult) {
        factorizationResultRepository.save(factorizationResult);
    }
}
