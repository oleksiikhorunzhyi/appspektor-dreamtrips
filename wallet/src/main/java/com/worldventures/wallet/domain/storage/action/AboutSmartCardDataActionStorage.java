package com.worldventures.wallet.domain.storage.action;

import android.support.annotation.Nullable;

import com.worldventures.core.janet.cache.CacheBundle;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.wallet.domain.entity.AboutSmartCardData;
import com.worldventures.wallet.domain.storage.disk.FirmwareDataStorage;
import com.worldventures.wallet.service.command.AboutSmartCardDataCommand;

public class AboutSmartCardDataActionStorage implements ActionStorage<AboutSmartCardData> {

   private final FirmwareDataStorage persistentStorage;

   public AboutSmartCardDataActionStorage(FirmwareDataStorage persistentStorage) {
      this.persistentStorage = persistentStorage;
   }

   @Override
   public void save(@Nullable CacheBundle params, AboutSmartCardData data) {
      persistentStorage.saveAboutSmartCardData(data);
   }

   @Override
   public AboutSmartCardData get(@Nullable CacheBundle action) {
      return persistentStorage.getAboutSmartCardData();
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return AboutSmartCardDataCommand.class;
   }
}
