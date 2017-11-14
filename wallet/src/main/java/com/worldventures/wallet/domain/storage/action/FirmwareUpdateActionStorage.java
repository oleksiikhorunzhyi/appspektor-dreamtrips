package com.worldventures.wallet.domain.storage.action;

import android.support.annotation.Nullable;

import com.worldventures.core.janet.cache.CacheBundle;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.wallet.service.firmware.FirmwareRepository;
import com.worldventures.wallet.service.firmware.command.FirmwareInfoCachedCommand;

public class FirmwareUpdateActionStorage implements ActionStorage<FirmwareUpdateData> {

   private final FirmwareRepository firmwareRepository;

   public FirmwareUpdateActionStorage(FirmwareRepository firmwareRepository) {
      this.firmwareRepository = firmwareRepository;
   }

   @Override
   public void save(@Nullable CacheBundle params, FirmwareUpdateData data) {
      firmwareRepository.setFirmwareUpdateData(data);
   }

   @Override
   public FirmwareUpdateData get(@Nullable CacheBundle action) {
      return firmwareRepository.getFirmwareUpdateData();
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return FirmwareInfoCachedCommand.class;
   }
}
