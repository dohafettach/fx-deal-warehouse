package bloomberg.fxdealswarehouse.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FxDealResponse {
    private String dealId;
    private String fromCurrency;
    private String toCurrency;
    private LocalDateTime dealTimestamp;
    private BigDecimal dealAmount;
    private LocalDateTime createdAt;
    private String message;
    public FxDealResponse() {
    }
    public FxDealResponse(String dealId, String fromCurrency, String toCurrency, LocalDateTime dealTimestamp, BigDecimal dealAmount, LocalDateTime createdAt, String message) {
        this.dealId = dealId;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.dealTimestamp = dealTimestamp;
        this.dealAmount = dealAmount;
        this.createdAt = createdAt;
        this.message = message;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }}
