package com.mwasilew.server_app.unit_tests.services_tests;

import com.mwasilew.server_app.NumberFactorizor;
import com.mwasilew.server_app.models.FactorizationResult;
import com.mwasilew.server_app.repositories.FactorizationRepository;
import org.hibernate.exception.SQLGrammarException;
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

    @Test
    public void givenFactorizationService_whenAskedForValueAboveAcceptedRange_thenResultForInvalidRequestIsReturned() {
        int request = NumberFactorizor.FACTORIZATION_RANGE + 1;
        Mockito.when(factorizationRepository.findById(request)).thenReturn(Optional.empty());
        Optional<FactorizationResult> expected = Optional.of(new FactorizationResult(request, ""));
        Optional<FactorizationResult> actual =  factorizationService.getFactorizationResultFor(request);
        assert actual.orElse(null) != null;
        assertEquals(expected.get().getNumber(), actual.orElse(null).getNumber());
        assertEquals(expected.get().getFactors(), actual.orElse(null).getFactors());
    }

    @Test
    public void givenFactorizationService_whenAskedForNegativeNumber_thenResultForInvalidRequestIsReturned() {
        int request = -1;
        Mockito.when(factorizationRepository.findById(request)).thenReturn(Optional.empty());
        Optional<FactorizationResult> expected = Optional.of(new FactorizationResult(request, ""));
        Optional<FactorizationResult> actual =  factorizationService.getFactorizationResultFor(request);
        assert actual.orElse(null) != null;
        assertEquals(expected.get().getNumber(), actual.orElse(null).getNumber());
        assertEquals(expected.get().getFactors(), actual.orElse(null).getFactors());
    }

    @Test
    public void givenFactorizationService_whenErrorOccurredWhileProcessingTheRequest_thenResultForInvalidRequestIsReturned() {
        int request = 17;
        Mockito.when(factorizationRepository.findById(request)).thenThrow(SQLGrammarException.class);
        Optional<FactorizationResult> expected = Optional.of(new FactorizationResult(request, ""));
        Optional<FactorizationResult> actual =  factorizationService.getFactorizationResultFor(request);
        assert actual.orElse(null) != null;
        assertEquals(expected.get().getNumber(), actual.orElse(null).getNumber());
        assertEquals(expected.get().getFactors(), actual.orElse(null).getFactors());
    }
}
