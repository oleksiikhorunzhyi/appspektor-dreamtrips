package com.worldventures.dreamtrips.modules.infopages.service.storage;


import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackType;
import com.worldventures.dreamtrips.modules.infopages.service.command.GetFeedbackCommand;

import java.util.List;

public class FeedbackTypeStorage implements ActionStorage<List<FeedbackType>> {

   private SnappyRepository snappyRepository;

   public FeedbackTypeStorage(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetFeedbackCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, List<FeedbackType> data) {
      snappyRepository.setFeedbackTypes(data);
   }

   @Override
   public List<FeedbackType> get(@Nullable CacheBundle action) {
      return snappyRepository.getFeedbackTypes();
   }
}
