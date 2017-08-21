package com.worldventures.dreamtrips.wallet.di.external;


import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.settings.model.FlagSetting;
import com.worldventures.dreamtrips.modules.settings.model.Setting;

import java.util.List;

public class WalletTrackingStatusStorageImpl implements WalletTrackingStatusStorage {

   private final SnappyRepository snappyRepository;

   public WalletTrackingStatusStorageImpl(SnappyRepository snappyRepository) {
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
