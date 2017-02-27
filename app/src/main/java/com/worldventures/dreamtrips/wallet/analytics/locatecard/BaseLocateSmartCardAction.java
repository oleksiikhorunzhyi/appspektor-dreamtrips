package com.worldventures.dreamtrips.wallet.analytics.locatecard;

import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;

import java.util.Locale;

public abstract class BaseLocateSmartCardAction extends WalletAnalyticsAction {

   @Attribute("coordinates") String coordinates;

   public void setLocation(double latitude, double longitude) {
      this.coordinates = String.format(Locale.US, "%f,%f", latitude, longitude);
   }
}
