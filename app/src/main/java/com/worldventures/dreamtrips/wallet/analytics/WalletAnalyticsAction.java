package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;

public abstract class WalletAnalyticsAction extends BaseAnalyticsAction {

   @Attribute("cid") String cid;
   @Attribute("lockstatus") String lockStatus;
   @Attribute("cardconnected") String cardConnected;
   @Attribute("batterystatus") String batteryStatus;
   @Attribute("currentversion") String currentVersion;

   public WalletAnalyticsAction() {}

   public WalletAnalyticsAction(String scId) {
      cid = scId;
   }

   public void setSmartCardAction(SmartCard smartCard, SmartCardStatus smartCardStatus,
         SmartCardFirmware smartCardFirmware) {
      if (smartCard == null) return;

      setSmartCardData(
            smartCard.smartCardId(),
            smartCardStatus.connectionStatus().isConnected(),
            smartCardStatus.lock(),
            smartCardStatus.batteryLevel());

      if (smartCardFirmware != null) {
         setCurrentVersion(smartCardFirmware.nordicAppVersion());
      }
   }

   public void setSmartCardData(String scId, boolean connected, boolean lockStatus, int batteryLevel) {
      cid = scId;
      setConnected(connected);
      if (connected) {
         setLockStatus(lockStatus);
         batteryStatus = batteryLevel != 0 ? Integer.toString(batteryLevel) : null;
      }
   }

   public void setSmartCardData(String scId, boolean connected, boolean lockStatus) {
      setSmartCardData(scId, connected, lockStatus, 0);
   }

   private void setSmartCardData(String scId) {
      cid = scId;
   }

   public void setSmartCardData(boolean connected) {
      setConnected(connected);
   }

   private void setConnected(boolean connected) {
      cardConnected = connected ? "Yes" : "No";
   }

   private void setLockStatus(boolean lockStatus) {
      this.lockStatus = lockStatus ? "Locked" : "Unlocked";
   }

   public void setCurrentVersion(String currentVersion) {
      this.currentVersion = currentVersion;
   }
}
