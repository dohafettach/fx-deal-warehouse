package bloomberg.fxdealswarehouse.dto;

import java.util.ArrayList;
import java.util.List;

public class FxDealBatchResponse {
    private int totalRequested;
    private int successCount;
    private int failureCount;
    private List<FxDealResponse> successfulDeals;
    private List<DealError> failedDeals;

    public FxDealBatchResponse() {
        this.successfulDeals = new ArrayList<>();
        this.failedDeals = new ArrayList<>();
    }

    public int getTotalRequested() {
        return totalRequested;
    }

    public void setTotalRequested(int totalRequested) {
        this.totalRequested = totalRequested;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public List<FxDealResponse> getSuccessfulDeals() {
        return successfulDeals;
    }

    public void setSuccessfulDeals(List<FxDealResponse> successfulDeals) {
        this.successfulDeals = successfulDeals;
    }

    public List<DealError> getFailedDeals() {
        return failedDeals;
    }

    public void setFailedDeals(List<DealError> failedDeals) {
        this.failedDeals = failedDeals;
    }
}
