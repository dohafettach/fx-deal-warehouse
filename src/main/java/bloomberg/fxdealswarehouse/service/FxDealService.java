package bloomberg.fxdealswarehouse.service;

import bloomberg.fxdealswarehouse.dto.*;
import bloomberg.fxdealswarehouse.entity.FxDeal;
import bloomberg.fxdealswarehouse.exception.DuplicateDealException;
import bloomberg.fxdealswarehouse.exception.InvalidDealException;
import bloomberg.fxdealswarehouse.repository.FxDealRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.List;

@Service
public class FxDealService {
    private final Logger logger = LoggerFactory.getLogger(FxDealService.class);
    private final FxDealRepository fxDealRepository;

    public FxDealService(FxDealRepository fxDealRepository) {
        this.fxDealRepository = fxDealRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public FxDealResponse importDeal(FxDealRequest request) {
        logger.info("Processing deal: {}", request.getDealId());

        if (fxDealRepository.existsByDealId(request.getDealId())) {
            throw new DuplicateDealException("Deal " + request.getDealId() + " already exists");
        }
        validateCurrency(request.getFromCurrency());
        validateCurrency(request.getToCurrency());
        if(request.getFromCurrency().equals(request.getToCurrency())){
            throw new InvalidDealException("From and To currency cannot be same");
        }
        FxDeal deal=new FxDeal(
                request.getDealId(),
                request.getFromCurrency(),
                request.getToCurrency(),
                request.getDealTimestamp(),
                request.getDealAmount()
        );
        FxDeal saved=fxDealRepository.save(deal);
        logger.info("Deal saved: {}", saved.getDealId());
        return createResponse(saved);
    }
    public FxDealBatchResponse importDealsInBatch(FxDealBatchRequest batchRequest) {
        logger.info("Processing batch import of {} deals", batchRequest.getDeals().size());
        FxDealBatchResponse response = new FxDealBatchResponse();
        response.setTotalRequested(batchRequest.getDeals().size());
        int rowNumber=1;
        for(FxDealRequest dealRequest:batchRequest.getDeals()){
            try{
                FxDealResponse dealResponse = importDeal(dealRequest);
                response.getSuccessfulDeals().add(dealResponse);
                response.setSuccessCount(response.getSuccessCount()+1);
            }
            catch(DuplicateDealException|InvalidDealException|IllegalArgumentException e){
                logger.warn("Deal failed at row {}: {}", rowNumber, e.getMessage());
                DealError error= new DealError(
                        dealRequest.getDealId(),
                        e.getMessage(),
                        rowNumber
                );
                response.getFailedDeals().add(error);
                response.setFailureCount(response.getFailureCount()+1);
            } catch (Exception e) {
                logger.error("Unexpected error at row {}: {}", rowNumber, e.getMessage());
                DealError error = new DealError(
                        dealRequest.getDealId(),
                        "Unexpected error: " + e.getMessage(),
                        rowNumber
                );
                response.getFailedDeals().add(error);
                response.setFailureCount(response.getFailureCount() + 1);
            }
            rowNumber++;
        }
        logger.info("Batch import completed: {} successful, {} failed",
                response.getSuccessCount(), response.getFailureCount());

        return response;
    }

        public List<FxDeal> getAllDeals() {
        return fxDealRepository.findAll();
    }
    private void validateCurrency(String code) {
        try{
            Currency.getInstance(code);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid currency"+code);
        }
    }
    private FxDealResponse createResponse(FxDeal deal){
        FxDealResponse response=new FxDealResponse();
        response.setDealId(deal.getDealId());
        response.setFromCurrency(deal.getFromCurrency());
        response.setToCurrency(deal.getToCurrency());
        response.setDealTimestamp(deal.getDealTimestamp());
        response.setDealAmount(deal.getDealAmount());
        response.setCreatedAt(deal.getCreatedAt());
        response.setMessage("Deal imported successfully");
        return response;
    }
}

