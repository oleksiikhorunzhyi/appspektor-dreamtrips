package com.worldventures.wallet.domain.storage.action;

import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.janet.cache.storage.MemoryStorage;
import com.worldventures.wallet.domain.entity.SmartCardStatus;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;

public class DeviceStateActionStorage extends MemoryStorage<SmartCardStatus> implements ActionStorage<SmartCardStatus> {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return DeviceStateCommand.class;
   }
}
