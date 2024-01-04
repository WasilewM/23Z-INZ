package com.mwasilew.server_app.integration_tests;

import com.mwasilew.server_app.controllers.FactorizationController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FactorizationControllerTests {

    @Autowired
    private FactorizationController controller;

    @Test
    public void givenFactorizationController_whenAskedForIntMaxPlusOne_thenResultForInvalidRequestIsReturned() {
        String intMaxPlusOne = "2147483648";
        Assertions.assertEquals(Optional.empty(), this.controller.getFactorizationResult(intMaxPlusOne));
    }

    @Test
    public void givenFactorizationController_whenAskedForNumberGreaterThanIntMax_thenResultForInvalidRequestIsReturned() {
        String requestBeyondRange = "3000000000";
        Assertions.assertEquals(Optional.empty(), this.controller.getFactorizationResult(requestBeyondRange));
    }

    @Test
    public void givenFactorizationController_whenAskedForIntMinMinusOne_thenResultForInvalidRequestIsReturned() {
        String intMinMinusOne = "-2147483649";
        Assertions.assertEquals(Optional.empty(), this.controller.getFactorizationResult(intMinMinusOne));
    }

    @Test
    public void givenFactorizationController_whenAskedForNumberSmallerThanIntMin_thenResultForInvalidRequestIsReturned() {
        String requestBeyondRange = "-9876543210";
        Assertions.assertEquals(Optional.empty(), this.controller.getFactorizationResult(requestBeyondRange));
    }
}
