package bloomberg.fxdealswarehouse.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fx_deals")
public class FxDeal {
    
    @Id
    @Column(name = "deal_id")
    private String dealId;
    
    @Column(name = "from_currency", length = 3, nullable = false)
    private String fromCurrency;
    
    @Column(name = "to_currency", length = 3, nullable = false)
    private String toCurrency;
    
    @Column(name = "deal_timestamp", nullable = false)
    private LocalDateTime dealTimestamp;
    
    @Column(name = "deal_amount", nullable = false)
    private BigDecimal dealAmount;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public FxDeal() {
    }
    
    public FxDeal(String dealId, String fromCurrency, String toCurrency, 
                  LocalDateTime dealTimestamp, BigDecimal dealAmount) {
        this.dealId = dealId;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.dealTimestamp = dealTimestamp;
        this.dealAmount = dealAmount;
    }
    
    @PrePersist
    public void setCreationDate() {
        this.createdAt = LocalDateTime.now();
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
}