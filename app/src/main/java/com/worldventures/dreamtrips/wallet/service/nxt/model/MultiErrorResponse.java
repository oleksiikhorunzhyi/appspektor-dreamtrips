package com.worldventures.dreamtrips.wallet.service.nxt.model;

import com.google.gson.annotations.SerializedName;

public class MultiErrorResponse {

   @SerializedName("Code") int code;

   @SerializedName("Message") String message;

   public int getCode() {
      return code;
   }

   public String getMessage() {
      return message;
   }
}
