package com.worldventures.wallet.service.credentials;

public class GoogleApiCredentials {

   private final String apiKey;

   public GoogleApiCredentials(String apiKey) {
      this.apiKey = apiKey;
   }

   public String getApiKey() {
      return apiKey;
   }
}
