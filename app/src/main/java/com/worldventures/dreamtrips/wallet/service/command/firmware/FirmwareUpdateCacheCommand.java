package com.worldventures.dreamtrips.wallet.service.command.firmware;


import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class FirmwareUpdateCacheCommand extends Command<FirmwareUpdateData> implements CachedAction<FirmwareUpdateData> {

   private FirmwareUpdateData data;
   private final boolean restoreOnly;

   public FirmwareUpdateCacheCommand() {
      restoreOnly = true;
   }

   public FirmwareUpdateCacheCommand(FirmwareUpdateData data) {
      this.data = data;
      restoreOnly = false;
   }

   @Override
   protected void run(CommandCallback<FirmwareUpdateData> callback) throws Throwable {
      callback.onSuccess(data);
   }

   @Override
   public FirmwareUpdateData getCacheData() {
      return data;
   }

   @Override
   public void onRestore(ActionHolder holder, FirmwareUpdateData cache) {
      this.data = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder().restoreFromCache(restoreOnly).saveToCache(!restoreOnly).build();
   }
}
