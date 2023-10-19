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
        Optional<FactorizationResult> cacheResult = factorizationResultService.getFactorizationResultFor(number);
        if (cacheResult.isEmpty()) {
            NumberFactorizor nr = new NumberFactorizor();
            ArrayList<Integer> res = nr.factorize(number);
            return Optional.of(new FactorizationResult(number, res.toString()));
        }
        return cacheResult;
    }
}
