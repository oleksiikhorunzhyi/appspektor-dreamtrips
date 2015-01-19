package com.worldventures.dreamtrips.core.model;

import java.io.Serializable;

public class Price implements Serializable {

    private double amount;
    private String currency;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return currency + String.valueOf(amount);
    }
}
