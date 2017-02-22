package com.worldventures.dreamtrips.wallet.domain.storage;


import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.service.command.device.SmartCardFirmwareCommand;

public class SmartCardFirmwareActionStorage extends MemoryStorage<SmartCardFirmware> implements ActionStorage<SmartCardFirmware> {
   @Override
   public Class<? extends CachedAction> getActionClass() {
      return SmartCardFirmwareCommand.class;
   }
}
