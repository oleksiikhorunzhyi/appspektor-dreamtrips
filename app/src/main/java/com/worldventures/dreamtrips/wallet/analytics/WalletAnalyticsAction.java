package com.worldventures.dreamtrips.wallet.analytics;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;

public abstract class WalletAnalyticsAction extends BaseAnalyticsAction {

   @Attribute("cid") String cid;
   @Attribute("lockstatus") String lockStatus;
   @Attribute("cardconnected") String cardConnected;
   @Attribute("batterystatus") String batteryLevel;
   @Attribute("currentversion") String currentVersion;

   public WalletAnalyticsAction() {}

   public WalletAnalyticsAction(String scId) {
      cid = scId;
   }

   void setSmartCardAction(@Nullable SmartCard smartCard, @Nullable SmartCardStatus smartCardStatus,
         @Nullable SmartCardFirmware smartCardFirmware) {

      setSmartCardData(
            fetchScId(smartCard),
            fetchScStatus(smartCard),
            fetchConnectionStatus(smartCardStatus),
            fetchLockState(smartCardStatus),
            fetchBatteryLevel(smartCardStatus));

      if (smartCardFirmware != null) {
         setCurrentVersion(smartCardFirmware.nordicAppVersion());
      }
   }

   @Nullable
   private String fetchScId(@Nullable SmartCard smartCard) {
      return smartCard == null ? null : smartCard.smartCardId();
   }

   @Nullable
   private SmartCard.CardStatus fetchScStatus(@Nullable SmartCard smartCard) {
      return smartCard == null ? null : smartCard.cardStatus();
   }

   @Nullable
   private ConnectionStatus fetchConnectionStatus(@Nullable SmartCardStatus smartCardStatus) {
      return smartCardStatus == null ? ConnectionStatus.DISCONNECTED : smartCardStatus.connectionStatus();
   }

   private boolean fetchLockState(@Nullable SmartCardStatus smartCardStatus) {
      return smartCardStatus == null || smartCardStatus.lock();
   }

   private int fetchBatteryLevel(@Nullable SmartCardStatus smartCardStatus) {
      return smartCardStatus == null ? 0 : smartCardStatus.batteryLevel();
   }

   private void setSmartCardData(@Nullable String scId, @Nullable SmartCard.CardStatus cardState, @Nullable ConnectionStatus connectionStatus,
         boolean lockStatus, int batteryLevel) {

      setSmartCardId(scId);

      boolean connected = connectionStatus != null && connectionStatus.isConnected();
      setConnected(connected);

      if (connected && cardState != null && cardState == SmartCard.CardStatus.ACTIVE) {
         setLockStatus(lockStatus);
         setBatteryLevel(batteryLevel);
      }
   }

   private void setSmartCardId(String scId) {
      cid = scId;
   }

   protected void setConnected(boolean connected) {
      cardConnected = connected ? "Yes" : "No";
   }

   private void setLockStatus(boolean lockStatus) {
      this.lockStatus = lockStatus ? "Locked" : "Unlocked";
   }

   private void setBatteryLevel(int batteryLevel) {
      this.batteryLevel = batteryLevel != 0 ? Integer.toString(batteryLevel) : null;
   }

   private void setCurrentVersion(String currentVersion) {
      this.currentVersion = currentVersion;
   }
}
