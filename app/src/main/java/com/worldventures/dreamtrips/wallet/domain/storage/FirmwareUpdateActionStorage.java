package com.worldventures.dreamtrips.wallet.domain.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.firmware.FirmwareRepository;
import com.worldventures.dreamtrips.wallet.service.firmware.command.FirmwareInfoCachedCommand;

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
