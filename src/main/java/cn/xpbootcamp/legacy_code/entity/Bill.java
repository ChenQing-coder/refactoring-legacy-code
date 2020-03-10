package cn.xpbootcamp.legacy_code.entity;

import cn.xpbootcamp.legacy_code.enums.STATUS;

import javax.transaction.InvalidTransactionException;

public class Bill {
    private Long buyerId;
    private Long sellerId;
    private Long createdTimestamp;
    private Double amount;
    private STATUS status;

    public Bill(Long buyerId, Long sellerId, Double amount) {
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.status = STATUS.TO_BE_EXECUTED;
        this.createdTimestamp = System.currentTimeMillis();
        this.amount = amount;
    }

    public void validateInfo() throws InvalidTransactionException {
        if (buyerId == null || (sellerId == null || amount < 0.0)) {
            throw new InvalidTransactionException("This is an invalid transaction");
        }
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public Double getAmount() {
        return amount;
    }
}
