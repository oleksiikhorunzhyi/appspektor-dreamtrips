package com.worldventures.dreamtrips.modules.trips.model;

import java.io.Serializable;
import java.text.DecimalFormat;

public class Price implements Serializable {

    DecimalFormat df = new DecimalFormat("#.00");

    public static final String USD = "USD";

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
        return getCurrencySymbol() + df.format(amount);
    }

    private String getCurrencySymbol() {
        String result = currency;
        if (currency.equals(USD)) {
            result = "$";
        }
        return result;
    }
}
