package com.mwasilew.server_app.controllers;

import com.mwasilew.server_app.models.FactorizationResult;
import com.mwasilew.server_app.services.FactorizationResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path="factorization_results")
public class FactorizationResultController {
    private final FactorizationResultService factorizationResultService;

    @Autowired
    public FactorizationResultController(FactorizationResultService factorizationResultService) {
        this.factorizationResultService = factorizationResultService;
    }


    @GetMapping
    public List<FactorizationResult> getDepartments () {
        return factorizationResultService.getFactors();
    }
}
