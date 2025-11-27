package bloomberg.fxdealswarehouse.controller;

import bloomberg.fxdealswarehouse.dto.FxDealBatchRequest;
import bloomberg.fxdealswarehouse.dto.FxDealBatchResponse;
import bloomberg.fxdealswarehouse.dto.FxDealRequest;
import bloomberg.fxdealswarehouse.dto.FxDealResponse;
import bloomberg.fxdealswarehouse.entity.FxDeal;
import bloomberg.fxdealswarehouse.service.FxDealService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deals")
public class FxDealController {
    private final Logger logger = LoggerFactory.getLogger(FxDealController.class);
    private final FxDealService fxDealService;
    public FxDealController(FxDealService fxDealService) {
        this.fxDealService = fxDealService;
    }
    @PostMapping
    public ResponseEntity<FxDealResponse> importDeal(@Valid @RequestBody FxDealRequest request) {
        logger.info("Import request received: {}", request.getDealId());
        FxDealResponse response = fxDealService.importDeal(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PostMapping("/batch")
    public ResponseEntity<FxDealBatchResponse> importBatch(@Valid @RequestBody FxDealBatchRequest request) {
        logger.info("Batch import request received: {}", request.getDeals().size());
        FxDealBatchResponse response = fxDealService.importDealsInBatch(request);
        if(response.getFailureCount()>0 &&response.getSuccessCount()>0){
            return new ResponseEntity<>(response,HttpStatus.MULTI_STATUS);
        }
        if (response.getFailureCount() == 0) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @GetMapping
    public ResponseEntity<List<FxDeal>> getAllDeals(){
        return ResponseEntity.ok(fxDealService.getAllDeals());
    }
}
