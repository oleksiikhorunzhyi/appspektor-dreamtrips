package com.worldventures.dreamtrips.wallet.domain.storage.action;

import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;

public class DeviceStateActionStorage extends MemoryStorage<SmartCardStatus> implements ActionStorage<SmartCardStatus> {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return DeviceStateCommand.class;
   }
}
