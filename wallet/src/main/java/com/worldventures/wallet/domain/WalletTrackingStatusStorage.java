package com.worldventures.wallet.domain;

public interface WalletTrackingStatusStorage {

   String SETTING_TRACKING_STATUS = "smartcard_location_tracking_enabled";

   void saveEnabledTracking(boolean enable);

   boolean isEnableTracking();
}
