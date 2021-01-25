package com.yurkov.model;

import java.math.BigDecimal;

/**
 * @author yevhenii yurkov
 * @since 0.0.1
 */
public class Transfer {
    private Long accountFromId;
    private Long accountToId;
    private BigDecimal amount;

    public Long getAccountFromId() {
        return accountFromId;
    }

    public void setAccountFromId(Long accountFromId) {
        this.accountFromId = accountFromId;
    }

    public Long getAccountToId() {
        return accountToId;
    }

    public void setAccountToId(Long accountToId) {
        this.accountToId = accountToId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
