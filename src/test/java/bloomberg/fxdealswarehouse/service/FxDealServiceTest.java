package bloomberg.fxdealswarehouse.service;

import bloomberg.fxdealswarehouse.dto.*;
import bloomberg.fxdealswarehouse.entity.FxDeal;
import bloomberg.fxdealswarehouse.exception.DuplicateDealException;
import bloomberg.fxdealswarehouse.exception.InvalidDealException;
import bloomberg.fxdealswarehouse.repository.FxDealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

 class FxDealServiceTest {
    @Mock
    private FxDealRepository fxDealRepository;
    @InjectMocks
    private FxDealService fxDealService;
    private FxDealRequest validRequest;
    private FxDeal savedDeal;
    @BeforeEach
    void setUp() {
        validRequest = new FxDealRequest(
                "DEAL001",
                "USD",
                "MAD",
                LocalDateTime.now(),
                new BigDecimal("1000.50")
        );

        savedDeal = new FxDeal(
                "DEAL001",
                "USD",
                "MAD",
                LocalDateTime.now(),
                new BigDecimal("1000.50")
        );
    }
    @Test
    void importDeal_WithValidRequest_ShouldSaveDeal() {
        when(fxDealRepository.existsByDealId("DEAL001")).thenReturn(false);
        when(fxDealRepository.save(any(FxDeal.class))).thenReturn(savedDeal);

        FxDealResponse response = fxDealService.importDeal(validRequest);

        assertNotNull(response);
        assertEquals("DEAL001", response.getDealId());
        assertEquals("USD", response.getFromCurrency());
        assertEquals("MAD", response.getToCurrency());
        assertEquals(new BigDecimal("1000.50"), response.getDealAmount());
        assertEquals("Deal imported successfully", response.getMessage());

        verify(fxDealRepository, times(1)).existsByDealId("DEAL001");
        verify(fxDealRepository, times(1)).save(any(FxDeal.class));
    }
    @Test
    void importDeal_withDuplicateDealId_ShouldThrowException() {
        when(fxDealRepository.existsByDealId("DEAL001")).thenReturn(true);
        DuplicateDealException exception = assertThrows(
                DuplicateDealException.class,
                () -> fxDealService.importDeal(validRequest)
        );
        assertEquals("Deal DEAL001 already exists", exception.getMessage());
        verify(fxDealRepository, times(1)).existsByDealId("DEAL001");
        verify(fxDealRepository, never()).save(any(FxDeal.class));

    }
    @Test
   void importDeal_withInvalidFromCurrency_ShouldThrowException() {
       FxDealRequest invalidRequest = new FxDealRequest(
               "DEAL002",
               "AAA",
               "MAD",
               LocalDateTime.now(),
               new BigDecimal("1000.50")
       );
       when(fxDealRepository.existsByDealId("DEAL002")).thenReturn(false);
       IllegalArgumentException exception = assertThrows(
               IllegalArgumentException.class,
               () -> fxDealService.importDeal(invalidRequest)
       );
       assertTrue(exception.getMessage().contains("Invalid currency"));
       verify(fxDealRepository, never()).save(any(FxDeal.class));
   }

    @Test
    void importDeal_withInvalidToCurrency_ShouldThrowException(){
        FxDealRequest invalidRequest = new FxDealRequest(
                "DEAL003",
                "USD",
                "BBB",
                LocalDateTime.now(),
                new BigDecimal("1000.50")
        );
        when(fxDealRepository.existsByDealId("DEAL003")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fxDealService.importDeal(invalidRequest)
        );

        assertTrue(exception.getMessage().contains("Invalid currency"));
        verify(fxDealRepository, never()).save(any(FxDeal.class));
    }
    @Test
    void importDeal_WithSameCurrencies_ShouldThrowException() {
        FxDealRequest samecurrencies = new FxDealRequest(
                "DEAL004",
                "USD",
                "USD",
                LocalDateTime.now(),
                new BigDecimal("1000.50")
        );
        when(fxDealRepository.existsByDealId("DEAL004")).thenReturn(false);
        InvalidDealException exception = assertThrows(
                InvalidDealException.class,
                () -> fxDealService.importDeal(samecurrencies)
        );
        assertEquals("From and To currency cannot be same", exception.getMessage());
        verify(fxDealRepository, never()).save(any(FxDeal.class));
    }
    @Test
    void importDealsInBatch_WithAllValidDeals_ShouldImportAll() {
        List<FxDealRequest> deals = new ArrayList<>();
        deals.add(new FxDealRequest("DEAL001", "USD", "EUR", LocalDateTime.now(), new BigDecimal("1000")));
        deals.add(new FxDealRequest("DEAL002", "MAD", "EUR", LocalDateTime.now(), new BigDecimal("2000")));
        deals.add(new FxDealRequest("DEAL003", "EUR", "CHF", LocalDateTime.now(), new BigDecimal("3000")));

        FxDealBatchRequest batchRequest = new FxDealBatchRequest(deals);

        when(fxDealRepository.existsByDealId(anyString())).thenReturn(false);
        when(fxDealRepository.save(any(FxDeal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FxDealBatchResponse response = fxDealService.importDealsInBatch(batchRequest);

        assertEquals(3, response.getTotalRequested());
        assertEquals(3, response.getSuccessCount());
        assertEquals(0, response.getFailureCount());
        assertEquals(3, response.getSuccessfulDeals().size());
        assertEquals(0, response.getFailedDeals().size());

        verify(fxDealRepository, times(3)).save(any(FxDeal.class));
    }

    @Test
    void importDealsInBatch_WithSomeInvalidDeals_ShouldImportValidOnesOnly() {
        List<FxDealRequest> deals = new ArrayList<>();
        deals.add(new FxDealRequest("DEAL001", "USD", "EUR", LocalDateTime.now(), new BigDecimal("1000")));
        deals.add(new FxDealRequest("DEAL002", "ABC", "EUR", LocalDateTime.now(), new BigDecimal("2000"))); // Changed to ABC
        deals.add(new FxDealRequest("DEAL003", "EUR", "CHF", LocalDateTime.now(), new BigDecimal("3000")));

        FxDealBatchRequest batchRequest = new FxDealBatchRequest(deals);

        when(fxDealRepository.existsByDealId(anyString())).thenReturn(false);
        when(fxDealRepository.save(any(FxDeal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FxDealBatchResponse response = fxDealService.importDealsInBatch(batchRequest);

        assertEquals(3, response.getTotalRequested());
        assertEquals(2, response.getSuccessCount());
        assertEquals(1, response.getFailureCount());
        assertEquals(2, response.getSuccessfulDeals().size());
        assertEquals(1, response.getFailedDeals().size());

        DealError error = response.getFailedDeals().get(0);
        assertEquals("DEAL002", error.getDealId());
        assertEquals(2, error.getRowNumber());
        assertTrue(error.getErrorMessage().contains("Invalid currency"));

        verify(fxDealRepository, times(2)).save(any(FxDeal.class));
    }
    @Test
    void importDealsInBatch_WithDuplicateDeals_ShouldRejectDuplicates() {
        List<FxDealRequest> deals = new ArrayList<>();
        deals.add(new FxDealRequest("DEAL001", "USD", "MAD", LocalDateTime.now(), new BigDecimal("1000")));
        deals.add(new FxDealRequest("DEAL002", "MAD", "EUR", LocalDateTime.now(), new BigDecimal("2000")));

        FxDealBatchRequest batchRequest = new FxDealBatchRequest(deals);

        when(fxDealRepository.existsByDealId("DEAL001")).thenReturn(false);
        when(fxDealRepository.existsByDealId("DEAL002")).thenReturn(true);
        when(fxDealRepository.save(any(FxDeal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FxDealBatchResponse response = fxDealService.importDealsInBatch(batchRequest);

        assertEquals(2, response.getTotalRequested());
        assertEquals(1, response.getSuccessCount());
        assertEquals(1, response.getFailureCount());

        DealError error = response.getFailedDeals().get(0);
        assertEquals("DEAL002", error.getDealId());
        assertTrue(error.getErrorMessage().contains("already exists"));

        verify(fxDealRepository, times(1)).save(any(FxDeal.class));
    }

    @Test
    void importDealsInBatch_WithSameCurrencyPair_ShouldRejectInvalidDeal() {
        List<FxDealRequest> deals = new ArrayList<>();
        deals.add(new FxDealRequest("DEAL001", "USD", "MAD", LocalDateTime.now(), new BigDecimal("1000")));
        deals.add(new FxDealRequest("DEAL002", "MAD", "MAD", LocalDateTime.now(), new BigDecimal("2000"))); // Same currency

        FxDealBatchRequest batchRequest = new FxDealBatchRequest(deals);

        when(fxDealRepository.existsByDealId(anyString())).thenReturn(false);
        when(fxDealRepository.save(any(FxDeal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FxDealBatchResponse response = fxDealService.importDealsInBatch(batchRequest);

        assertEquals(2, response.getTotalRequested());
        assertEquals(1, response.getSuccessCount());
        assertEquals(1, response.getFailureCount());

        DealError error = response.getFailedDeals().get(0);
        assertEquals("DEAL002", error.getDealId());
        assertEquals("From and To currency cannot be same", error.getErrorMessage());

        verify(fxDealRepository, times(1)).save(any(FxDeal.class));
    }

    @Test
    void getAllDeals_ShouldReturnAllDeals() {
        List<FxDeal> deals = new ArrayList<>();
        deals.add(savedDeal);

        when(fxDealRepository.findAll()).thenReturn(deals);

        List<FxDeal> result = fxDealService.getAllDeals();

        assertEquals(1, result.size());
        assertEquals("DEAL001", result.get(0).getDealId());

        verify(fxDealRepository, times(1)).findAll();
    }

    @Test
    void getAllDeals_WhenNoDealExists_ShouldReturnEmptyList() {
        when(fxDealRepository.findAll()).thenReturn(new ArrayList<>());

        List<FxDeal> result = fxDealService.getAllDeals();

        assertEquals(0, result.size());
        verify(fxDealRepository, times(1)).findAll();
    }
}
