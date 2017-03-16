package com.worldventures.dreamtrips.wallet.analytics.locatecard.action;

import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;

import java.util.Locale;

public abstract class BaseLocateSmartCardAction extends WalletAnalyticsAction {

   @Attribute("coordinates") String coordinates;

   public void setLocation(WalletCoordinates walletCoordinates) {
      this.coordinates = String.format(Locale.US, "%f,%f", walletCoordinates.lat(), walletCoordinates.lng());
   }
}
