package com.worldventures.dreamtrips.wallet;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.wallet.service.credentials.GoogleApiCredentials;
import com.worldventures.wallet.service.credentials.GoogleApiCredentialsProvider;

public class WalletGoogleApiCredentialsProvider implements GoogleApiCredentialsProvider {

   @Override
   public GoogleApiCredentials provideGoogleApiCredentials() {
      return new GoogleApiCredentials(BuildConfig.GOOGLE_PLACES_API_KEY);
   }
}
