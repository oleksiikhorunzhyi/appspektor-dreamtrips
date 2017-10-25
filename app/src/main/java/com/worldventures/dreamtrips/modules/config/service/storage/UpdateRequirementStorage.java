package com.worldventures.dreamtrips.modules.config.service.storage;

import android.support.annotation.Nullable;

import com.worldventures.core.janet.cache.CacheBundle;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.config.model.Configuration;
import com.worldventures.dreamtrips.modules.config.service.command.ConfigurationCommand;

public class UpdateRequirementStorage implements ActionStorage<Configuration> {

   private SnappyRepository snappyRepository;

   public UpdateRequirementStorage(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return ConfigurationCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, Configuration data) {
      snappyRepository.saveAppUpdateRequirement(data);
   }

   @Override
   public Configuration get(@Nullable CacheBundle action) {
      return snappyRepository.getAppUpdateRequirement();
   }
}
