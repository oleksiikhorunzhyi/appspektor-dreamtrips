package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

public abstract class WalletAnalyticsAction extends BaseAnalyticsAction {

   @Attribute("cid") String cid = "";
   @Attribute("lockstatus") String lockStatus = "";
   @Attribute("cardconnected") String cardConnected = "";
   @Attribute("batterystatus") String batteryStatus = "";

   WalletAnalyticsAction() {}

   WalletAnalyticsAction(String scId) {
      cid = scId;
   }

   public void setSmartCardAction(SmartCard smartCard) {
      if (smartCard == null) return;

      setSmartCardData(
            smartCard.smartCardId(),
            smartCard.connectionStatus().isConnected(),
            smartCard.lock(),
            String.valueOf(smartCard.batteryLevel()));
   }

   public void setSmartCardData(String scId, boolean connected, boolean lockStatus, String battaryLevel) {
      cid = scId;
      setConnected(connected);
      if (connected) {
         setLockStatus(lockStatus);
         batteryStatus = battaryLevel;
      }
   }

   public void setSmartCardData(String scId, boolean connected, boolean lockStatus) {
      setSmartCardData(scId, connected, lockStatus, "");
   }

   public void setSmartCardData(String scId) {
      cid = scId;
   }

   private void setConnected(boolean connected) {
      cardConnected = connected ? "Yes" : "No";
   }

   private void setLockStatus(boolean lockStatus) {
      this.lockStatus = lockStatus ? "Locked" : "Unlocked";
   }
}
