package com.worldventures.dreamtrips.wallet.domain.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.service.command.device.SmartCardFirmwareCommand;

public class SmartCardFirmwareActionStorage implements ActionStorage<SmartCardFirmware> {

   private final SnappyRepository snappyRepository;

   public SmartCardFirmwareActionStorage(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return SmartCardFirmwareCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, SmartCardFirmware data) {
      snappyRepository.saveSmartCardFirmware(data);
   }

   @Override
   public SmartCardFirmware get(@Nullable CacheBundle action) {
      return snappyRepository.getSmartCardFirmware();
   }
}
