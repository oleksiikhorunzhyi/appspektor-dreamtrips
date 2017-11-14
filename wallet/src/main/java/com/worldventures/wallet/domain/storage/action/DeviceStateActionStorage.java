package com.worldventures.wallet.domain.storage.action;

import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.core.janet.cache.storage.MemoryStorage;
import com.worldventures.wallet.domain.entity.SmartCardStatus;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;

public class DeviceStateActionStorage extends MemoryStorage<SmartCardStatus> implements ActionStorage<SmartCardStatus> {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return DeviceStateCommand.class;
   }
}
