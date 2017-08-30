package com.worldventures.dreamtrips.wallet.domain.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.wallet.domain.entity.AboutSmartCardData;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.WalletPersistentStorage;
import com.worldventures.dreamtrips.wallet.service.command.AboutSmartCardDataCommand;

public class AboutSmartCardDataActionStorage implements ActionStorage<AboutSmartCardData> {

   private WalletPersistentStorage persistentStorage;

   public AboutSmartCardDataActionStorage(WalletPersistentStorage persistentStorage) {
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
