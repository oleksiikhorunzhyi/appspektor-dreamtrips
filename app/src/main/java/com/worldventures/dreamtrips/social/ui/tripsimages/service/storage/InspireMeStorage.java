package com.worldventures.dreamtrips.social.ui.tripsimages.service.storage;

import com.worldventures.janet.cache.CacheBundle;
import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetInspireMePhotosCommand;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
         storage.save(params, new ArrayList<>(data));
      } else if (params.get(LOAD_MORE)) {
         List<Inspiration> cachedItems = fetchCache(params);
         cachedItems.addAll(data);
         storage.save(params, cachedItems);
      }
   }

   @Override
   public List<Inspiration> get(@Nullable CacheBundle params) {
      return fetchCache(params);
   }

   private List<Inspiration> fetchCache(@Nullable CacheBundle params) {
      List<Inspiration> cachedItems = storage.get(params);
      if (cachedItems != null) {
         return new ArrayList<>(cachedItems);
      } else {
         return new ArrayList<>();
      }
   }

}
