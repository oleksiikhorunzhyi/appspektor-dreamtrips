package com.worldventures.wallet.analytics.locatecard.action;

import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates;

import java.util.Locale;

public abstract class BaseLocateSmartCardAction extends WalletAnalyticsAction {

   @Attribute("coordinates") String coordinates;

   public void setLocation(WalletCoordinates walletCoordinates) {
      this.coordinates = String.format(Locale.US, "%f,%f", walletCoordinates.lat(), walletCoordinates.lng());
   }
}
