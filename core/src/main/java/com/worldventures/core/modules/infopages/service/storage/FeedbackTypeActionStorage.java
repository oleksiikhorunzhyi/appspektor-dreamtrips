package com.worldventures.core.modules.infopages.service.storage;


import android.support.annotation.Nullable;

import com.worldventures.janet.cache.CacheBundle;
import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.core.modules.infopages.model.FeedbackType;
import com.worldventures.core.modules.infopages.service.command.GetFeedbackCommand;

import java.util.List;

public class FeedbackTypeActionStorage implements ActionStorage<List<FeedbackType>> {

   private final InfopagesStorage storage;

   public FeedbackTypeActionStorage(InfopagesStorage storage) {
      this.storage = storage;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetFeedbackCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, List<FeedbackType> data) {
      storage.setFeedbackTypes(data);
   }

   @Override
   public List<FeedbackType> get(@Nullable CacheBundle action) {
      return storage.getFeedbackTypes();
   }
}
