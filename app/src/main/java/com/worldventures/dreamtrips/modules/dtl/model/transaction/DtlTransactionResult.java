package com.worldventures.dreamtrips.modules.dtl.model.transaction;

import com.google.gson.annotations.SerializedName;

public class DtlTransactionResult {

   @SerializedName("id")
   String id;

   @SerializedName("credited_amount")
   double creditedAmount;

   @SerializedName("current_balance")
   double currentBalance;

   public String getId() {
      return id;
   }
   public double getEarnedPoints() {
      return creditedAmount;
   }
   public double getTotal() {
      return currentBalance;
   }

}
