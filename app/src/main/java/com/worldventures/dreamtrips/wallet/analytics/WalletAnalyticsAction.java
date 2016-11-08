package com.worldventures.dreamtrips.wallet.analytics;


import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

public abstract class WalletAnalyticsAction extends BaseAnalyticsAction {

   @Attribute("cid") String cid = "";
   @Attribute("lockstatus") String lockStatus = "";
   @Attribute("cardconnected") String cardConnected = "";
   @Attribute("batterystatus") String batteryStatus = "";

   public void setSmartCardAction(SmartCard smartCard) {
      if (smartCard != null) {
         boolean smartCardConnected = smartCard.connectionStatus() == SmartCard.ConnectionStatus.CONNECTED;
         cardConnected = smartCardConnected ? "Yes" : "No";
         cid = smartCard.smartCardId();

         if (smartCardConnected) {
            lockStatus = smartCard.lock() ? "Locked" : "Unlocked";
            batteryStatus = String.valueOf(smartCard.batteryLevel());
         }
      }
   }
}
