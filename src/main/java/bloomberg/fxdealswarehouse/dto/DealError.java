package bloomberg.fxdealswarehouse.dto;

public class DealError {

    private String dealId;
    private String errorMessage;
    private int rowNumber;

    public DealError() {
    }

    public DealError(String dealId, String errorMessage, int rowNumber) {
        this.dealId = dealId;
        this.errorMessage = errorMessage;
        this.rowNumber = rowNumber;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }
}