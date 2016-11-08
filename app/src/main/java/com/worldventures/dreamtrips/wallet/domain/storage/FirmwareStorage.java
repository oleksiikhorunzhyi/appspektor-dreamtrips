package com.worldventures.dreamtrips.wallet.domain.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.Storage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.command.firmware.FirmwareUpdateCacheCommand;

import java.util.Arrays;
import java.util.List;

public class FirmwareStorage implements Storage<FirmwareUpdateData>, MultipleActionStorage<FirmwareUpdateData> {

   private final SnappyRepository db;

   public FirmwareStorage(SnappyRepository snappyRepository) {
      this.db = snappyRepository;
   }

   @Override
   public List<Class<? extends CachedAction>> getActionClasses() {
      return Arrays.asList(FirmwareUpdateCacheCommand.class);
   }

   @Override
   public void save(@Nullable CacheBundle params, FirmwareUpdateData data) {
      db.saveFirmwareUpdateData(data);
   }

   @Override
   public FirmwareUpdateData get(@Nullable CacheBundle action) {
      return db.getFirmwareUpdateData();
   }
}
