package bloomberg.fxdealswarehouse.dto;

import bloomberg.fxdealswarehouse.entity.FxDeal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FxDealRequest {
    @NotBlank(message = "Deal ID is required")
    private String dealId;
    @NotBlank(message = "From currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "From currency must be a valid 3-letter ISO code")
    private String fromCurrency;
    @NotBlank(message = "To currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "To currency must be a valid 3-letter ISO code")
    private String toCurrency;
    @NotNull(message = "Deal timestamp is required")
    private LocalDateTime dealTimestamp;
    @NotNull(message = "Deal amount is required")
    @DecimalMin(value = "0.01", message = "Deal amount must be positive")
    private BigDecimal  dealAmount;
    public FxDealRequest() {
    }
    public FxDealRequest(String dealId, String fromCurrency, String toCurrency, LocalDateTime dealTimestamp, BigDecimal  dealAmount) {
        this.dealId = dealId;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.dealTimestamp = dealTimestamp;
        this.dealAmount = dealAmount;
    }
    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public LocalDateTime getDealTimestamp() {
        return dealTimestamp;
    }

    public void setDealTimestamp(LocalDateTime dealTimestamp) {
        this.dealTimestamp = dealTimestamp;
    }

    public BigDecimal getDealAmount() {
        return dealAmount;
    }

    public void setDealAmount(BigDecimal dealAmount) {
        this.dealAmount = dealAmount;
    }

}
