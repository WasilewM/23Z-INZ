package com.mwasilew.server_app.unit_tests.services_tests;

import com.mwasilew.server_app.models.FactorizationResult;
import com.mwasilew.server_app.repositories.FactorizationRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class FactorizationServiceTest {
    @TestConfiguration
    public static class FactorizationServiceTestContextConfiguration {
        @Bean
        public FactorizationServiceWithMethodsCallingCounters factorizationService(FactorizationRepository fr) {
            return new FactorizationServiceWithMethodsCallingCounters(fr);
        }
    }

    @Autowired
    private FactorizationServiceWithMethodsCallingCounters factorizationService;
    @MockBean
    private FactorizationRepository factorizationRepository;

    @Before
    public void setUp() {
        factorizationService.resetMethodCallsCounters();
    }

    @Test
    public void givenFactorizationServiceWithMockedValueInDB_whenAskedForMockedValue_thenCalculationsAreNotPerformed() {
        int request = 5;
        Optional<FactorizationResult> factorizationResults = Optional.of(new FactorizationResult(5, "[1, 5]"));
        Mockito.when(factorizationRepository.findById(request)).thenReturn(factorizationResults);
        factorizationService.getFactorizationResultFor(request);

        assertEquals(0, factorizationService.handleRequestCalculationsMethodCalls);
        assertEquals(0, factorizationService.calculateFactorizationResultMethodCalls);
        assertEquals(0, factorizationService.saveFactorizationResultMethodCalls);
    }

    @Test
    public void givenFactorizationServiceWithoutValuesInDB_whenAskedForValue_thenCalculationsArePerformed() {
        int request = 5;
        Mockito.when(factorizationRepository.findById(request)).thenReturn(Optional.empty());
        factorizationService.getFactorizationResultFor(request);

        assertEquals(1, factorizationService.handleRequestCalculationsMethodCalls);
        assertEquals(1, factorizationService.calculateFactorizationResultMethodCalls);
        assertEquals(1, factorizationService.saveFactorizationResultMethodCalls);
    }
}
