package bloomberg.fxdealswarehouse.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class FxDealBatchRequest {
    @NotNull(message = "Deals list cannot be null")
    @NotEmpty(message = "Deals list cannot be empty")
    private List<FxDealRequest> deals;
    public FxDealBatchRequest() {
    }
    public FxDealBatchRequest(List<FxDealRequest> deals) {
        this.deals = deals;
    }

    public List<FxDealRequest> getDeals() {
        return deals;
    }

    public void setDeals(List<FxDealRequest> deals) {
        this.deals = deals;
    }
}
