package com.worldventures.dreamtrips.social.ui.tripsimages.service.storage;

import com.worldventures.core.janet.cache.CacheBundle;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.YSBHPhoto;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetYSBHPhotosCommand;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class YsbhPhotoStorage implements ActionStorage<List<YSBHPhoto>> {

   public static final String RELOAD = "RELOAD";
   public static final String LOAD_MORE = "LOAD_MORE";

   private final MemoryStorage<List<YSBHPhoto>> storage = new MemoryStorage<>();

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetYSBHPhotosCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, List<YSBHPhoto> data) {
      if (params.get(RELOAD)) {
         storage.save(params, new ArrayList<>(data));
      } else if (params.get(LOAD_MORE)) {
         List<YSBHPhoto> cachedItems = fetchCache(params);
         cachedItems.addAll(data);
         storage.save(params, cachedItems);
      }
   }

   @Override
   public List<YSBHPhoto> get(@Nullable CacheBundle params) {
      return fetchCache(params);
   }

   private List<YSBHPhoto> fetchCache(@Nullable CacheBundle params) {
      List<YSBHPhoto> cachedItems = storage.get(params);
      if (cachedItems != null) {
         return new ArrayList<>(cachedItems);
      } else {
         return new ArrayList<>();
      }
   }

}
