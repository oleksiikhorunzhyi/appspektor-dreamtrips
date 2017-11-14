package com.worldventures.dreamtrips.social.ui.tripsimages.service.storage;

import android.support.annotation.Nullable;

import com.worldventures.core.janet.cache.CacheBundle;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetInspireMePhotosCommand;

import java.util.List;

public class InspireMeStorage implements ActionStorage<List<Inspiration>> {

   public static final String RELOAD = "RELOAD";
   public static final String LOAD_MORE = "LOAD_MORE";

   private final MemoryStorage<List<Inspiration>> storage = new MemoryStorage<>();

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetInspireMePhotosCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, List<Inspiration> data) {
      if (params.get(RELOAD)) {
         storage.save(params, data);
      } else if (params.get(LOAD_MORE)) {
         List<Inspiration> cachedItems = storage.get(params);
         cachedItems.addAll(data);
         storage.save(params, cachedItems);
      }
   }

   @Override
   public List<Inspiration> get(@Nullable CacheBundle action) {
      return storage.get(action);
   }
}
