package com.worldventures.dreamtrips.wallet;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.wallet.service.nxt.NxtIdConfigsProvider;

public class NxtIdConfigsProviderImlp implements NxtIdConfigsProvider {

   @Override
   public String nxtidApi() {
      return BuildConfig.NXT_API;
   }

   @Override
   public long apiTimeoutSec() {
      return BuildConfig.API_TIMEOUT_SEC;
   }

   @Override
   public String nxtidSessionApi() {
      return BuildConfig.DreamTripsApi;
   }

   @Override
   public String apiVersion() {
      return BuildConfig.API_VERSION;
   }
}
