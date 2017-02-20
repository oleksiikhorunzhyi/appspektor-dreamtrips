package com.worldventures.dreamtrips.wallet.service.command.device;

import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;

public class DeviceStateMemoryStorage extends MemoryStorage<SmartCardStatus> implements ActionStorage<SmartCardStatus> {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return DeviceStateCommand.class;
   }
}
