package com.worldventures.dreamtrips.wallet;

import com.worldventures.core.modules.settings.model.FlagSetting;
import com.worldventures.core.modules.settings.model.Setting;
import com.worldventures.core.modules.settings.storage.SettingsStorage;
import com.worldventures.wallet.domain.WalletTrackingStatusStorage;

import java.util.List;

public class WalletTrackingStatusStorageImpl implements WalletTrackingStatusStorage {

   private final SettingsStorage snappyRepository;

   public WalletTrackingStatusStorageImpl(SettingsStorage snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public void saveEnabledTracking(boolean enable) {
      FlagSetting trackingStatusSetting = searchTrackingStatusSetting();
      final List<Setting> settings = snappyRepository.getSettings();
      if (trackingStatusSetting != null) {
         int trackingStatusPosition = settings.indexOf(trackingStatusSetting);
         trackingStatusSetting.setValue(enable);
         settings.set(trackingStatusPosition, trackingStatusSetting);
      } else {
         trackingStatusSetting = new FlagSetting(SETTING_TRACKING_STATUS, Setting.Type.FLAG, enable);
         settings.add(trackingStatusSetting);
      }
      snappyRepository.saveSettings(settings, false);
   }

   @Override
   public boolean isEnableTracking() {
      final FlagSetting trackingStatusSetting = searchTrackingStatusSetting();
      return trackingStatusSetting != null && trackingStatusSetting.getValue();
   }

   private FlagSetting searchTrackingStatusSetting() {
      final List<Setting> settings = snappyRepository.getSettings();
      FlagSetting trackingStatusSetting = null;
      for (Setting setting : settings) {
         if (setting.getName().equals(SETTING_TRACKING_STATUS)) {
            trackingStatusSetting = (FlagSetting) setting;
            break;
         }
      }
      return trackingStatusSetting;
   }
}
