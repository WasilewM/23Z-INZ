package com.mwasilew.server_app.controllers;

import com.mwasilew.server_app.models.FactorizationResult;
import com.mwasilew.server_app.services.FactorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(path= "factorize")
public class FactorizationController {
    private final FactorizationService factorizationService;

    @Autowired
    public FactorizationController(FactorizationService factorizationService) {
        this.factorizationService = factorizationService;
    }


    @GetMapping("{number}")
    public Optional<FactorizationResult> getFactorizationResult(@PathVariable int number) {
        return factorizationService.getFactorizationResultFor(number);
    }
}
