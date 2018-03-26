package com.worldventures.dreamtrips.modules.trips.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class Price implements Serializable {

   private static final String USD = "USD";

   private double amount;
   private String currency;

   public Price(double amount, String currency) {
      this.amount = amount;
      this.currency = currency;
   }

   public Price() {
      // This constructor is intentionally empty. Nothing special is needed here.
   }

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
      String currencySymbol = currency;
      if (currency.equals(USD)) {
         currencySymbol = "$";
      }
      return currencySymbol + String.format("%.02f", amount);
   }
}
