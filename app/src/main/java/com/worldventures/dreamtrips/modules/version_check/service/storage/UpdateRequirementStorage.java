package com.worldventures.dreamtrips.modules.version_check.service.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.version_check.model.UpdateRequirement;
import com.worldventures.dreamtrips.modules.version_check.service.command.VersionCheckCommand;

public class UpdateRequirementStorage implements ActionStorage<UpdateRequirement> {

   private SnappyRepository snappyRepository;

   public UpdateRequirementStorage(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return VersionCheckCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, UpdateRequirement data) {
      snappyRepository.saveAppUpdateRequirement(data);
   }

   @Override
   public UpdateRequirement get(@Nullable CacheBundle action) {
      return snappyRepository.getAppUpdateRequirement();
   }
}
