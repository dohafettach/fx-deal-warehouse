package bloomberg.fxdealswarehouse.controller;

import bloomberg.fxdealswarehouse.dto.*;
import bloomberg.fxdealswarehouse.entity.FxDeal;
import bloomberg.fxdealswarehouse.exception.DuplicateDealException;
import bloomberg.fxdealswarehouse.exception.InvalidDealException;
import bloomberg.fxdealswarehouse.service.FxDealService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FxDealController.class)
class FxDealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FxDealService fxDealService;

    private FxDealRequest validRequest;
    private FxDealResponse validResponse;

    @BeforeEach
    void setUp() {
        validRequest = new FxDealRequest(
                "DEAL001",
                "USD",
                "MAD",
                LocalDateTime.now().minusDays(1),
                new BigDecimal("1000.50")
        );

        validResponse = new FxDealResponse();
        validResponse.setDealId("DEAL001");
        validResponse.setFromCurrency("USD");
        validResponse.setToCurrency("EUR");
        validResponse.setDealTimestamp(LocalDateTime.now().minusDays(1));
        validResponse.setDealAmount(new BigDecimal("1000.50"));
        validResponse.setCreatedAt(LocalDateTime.now());
        validResponse.setMessage("Deal imported successfully");
    }

    @Test
    void importDeal_WithValidRequest_ShouldReturnCreated() throws Exception {
        when(fxDealService.importDeal(any(FxDealRequest.class))).thenReturn(validResponse);

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dealId").value("DEAL001"))
                .andExpect(jsonPath("$.fromCurrency").value("USD"))
                .andExpect(jsonPath("$.toCurrency").value("EUR"))
                .andExpect(jsonPath("$.dealAmount").value(1000.50))
                .andExpect(jsonPath("$.message").value("Deal imported successfully"));

        verify(fxDealService, times(1)).importDeal(any(FxDealRequest.class));
    }

    @Test
    void importDeal_WithDuplicateDeal_ShouldReturnConflict() throws Exception {
        when(fxDealService.importDeal(any(FxDealRequest.class)))
                .thenThrow(new DuplicateDealException("Deal DEAL001 already exists"));

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Duplicate Deal"))
                .andExpect(jsonPath("$.message").value("Deal DEAL001 already exists"));

        verify(fxDealService, times(1)).importDeal(any(FxDealRequest.class));
    }

    @Test
    void importDeal_WithInvalidCurrency_ShouldReturnBadRequest() throws Exception {
        when(fxDealService.importDeal(any(FxDealRequest.class)))
                .thenThrow(new InvalidDealException("Invalid currency"));

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid Deal"));

        verify(fxDealService, times(1)).importDeal(any(FxDealRequest.class));
    }

    @Test
    void importDeal_WithMissingDealId_ShouldReturnBadRequest() throws Exception {
        FxDealRequest invalidRequest = new FxDealRequest(
                null,  // Missing deal ID
                "USD",
                "EUR",
                LocalDateTime.now(),
                new BigDecimal("1000.50")
        );

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));

        verify(fxDealService, never()).importDeal(any(FxDealRequest.class));
    }

    @Test
    void importDeal_WithInvalidCurrencyFormat_ShouldReturnBadRequest() throws Exception {
        FxDealRequest invalidRequest = new FxDealRequest(
                "DEAL001",
                "US",
                "MAD",
                LocalDateTime.now(),
                new BigDecimal("1000.50")
        );

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));

        verify(fxDealService, never()).importDeal(any(FxDealRequest.class));
    }

    @Test
    void importDeal_WithNegativeAmount_ShouldReturnBadRequest() throws Exception {
        FxDealRequest invalidRequest = new FxDealRequest(
                "DEAL001",
                "USD",
                "MAD",
                LocalDateTime.now(),
                new BigDecimal("-100")
        );

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(fxDealService, never()).importDeal(any(FxDealRequest.class));
    }

    @Test
    void importBatch_WithAllValidDeals_ShouldReturnCreated() throws Exception {
        List<FxDealRequest> deals = new ArrayList<>();
        deals.add(new FxDealRequest("DEAL001", "USD", "MAD", LocalDateTime.now(), new BigDecimal("1000")));
        deals.add(new FxDealRequest("DEAL002", "MAD", "EUR", LocalDateTime.now(), new BigDecimal("2000")));

        FxDealBatchRequest batchRequest = new FxDealBatchRequest(deals);

        FxDealBatchResponse batchResponse = new FxDealBatchResponse();
        batchResponse.setTotalRequested(2);
        batchResponse.setSuccessCount(2);
        batchResponse.setFailureCount(0);

        when(fxDealService.importDealsInBatch(any(FxDealBatchRequest.class)))
                .thenReturn(batchResponse);

        mockMvc.perform(post("/api/deals/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalRequested").value(2))
                .andExpect(jsonPath("$.successCount").value(2))
                .andExpect(jsonPath("$.failureCount").value(0));

        verify(fxDealService, times(1)).importDealsInBatch(any(FxDealBatchRequest.class));
    }

    @Test
    void importBatch_WithPartialSuccess_ShouldReturnMultiStatus() throws Exception {
        List<FxDealRequest> deals = new ArrayList<>();
        deals.add(new FxDealRequest("DEAL001", "USD", "MAD", LocalDateTime.now(), new BigDecimal("1000")));
        deals.add(new FxDealRequest("DEAL002", "MAD", "EUR", LocalDateTime.now(), new BigDecimal("2000")));

        FxDealBatchRequest batchRequest = new FxDealBatchRequest(deals);

        FxDealBatchResponse batchResponse = new FxDealBatchResponse();
        batchResponse.setTotalRequested(2);
        batchResponse.setSuccessCount(1);
        batchResponse.setFailureCount(1);
        batchResponse.getFailedDeals().add(new DealError("DEAL002", "Duplicate deal", 2));

        when(fxDealService.importDealsInBatch(any(FxDealBatchRequest.class)))
                .thenReturn(batchResponse);

        mockMvc.perform(post("/api/deals/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isMultiStatus())
                .andExpect(jsonPath("$.totalRequested").value(2))
                .andExpect(jsonPath("$.successCount").value(1))
                .andExpect(jsonPath("$.failureCount").value(1));

        verify(fxDealService, times(1)).importDealsInBatch(any(FxDealBatchRequest.class));
    }

    @Test
    void importBatch_WithAllFailures_ShouldReturnBadRequest() throws Exception {
        List<FxDealRequest> deals = new ArrayList<>();
        deals.add(new FxDealRequest("DEAL001", "USD", "MAD", LocalDateTime.now(), new BigDecimal("1000")));

        FxDealBatchRequest batchRequest = new FxDealBatchRequest(deals);

        FxDealBatchResponse batchResponse = new FxDealBatchResponse();
        batchResponse.setTotalRequested(1);
        batchResponse.setSuccessCount(0);
        batchResponse.setFailureCount(1);

        when(fxDealService.importDealsInBatch(any(FxDealBatchRequest.class)))
                .thenReturn(batchResponse);

        mockMvc.perform(post("/api/deals/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.totalRequested").value(1))
                .andExpect(jsonPath("$.successCount").value(0))
                .andExpect(jsonPath("$.failureCount").value(1));

        verify(fxDealService, times(1)).importDealsInBatch(any(FxDealBatchRequest.class));
    }

    @Test
    void importBatch_WithEmptyList_ShouldReturnBadRequest() throws Exception {
        FxDealBatchRequest emptyRequest = new FxDealBatchRequest(new ArrayList<>());

        mockMvc.perform(post("/api/deals/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());

        verify(fxDealService, never()).importDealsInBatch(any(FxDealBatchRequest.class));
    }

    @Test
    void getAllDeals_WithExistingDeals_ShouldReturnList() throws Exception {
        List<FxDeal> deals = new ArrayList<>();
        FxDeal deal1 = new FxDeal("DEAL001", "USD", "MAD", LocalDateTime.now(), new BigDecimal("1000"));
        FxDeal deal2 = new FxDeal("DEAL002", "MAD", "EUR", LocalDateTime.now(), new BigDecimal("2000"));
        deals.add(deal1);
        deals.add(deal2);

        when(fxDealService.getAllDeals()).thenReturn(deals);

        mockMvc.perform(get("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].dealId").value("DEAL001"))
                .andExpect(jsonPath("$[1].dealId").value("DEAL002"));

        verify(fxDealService, times(1)).getAllDeals();
    }

    @Test
    void getAllDeals_WithNoDeals_ShouldReturnEmptyList() throws Exception {
        when(fxDealService.getAllDeals()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(fxDealService, times(1)).getAllDeals();
    }
}