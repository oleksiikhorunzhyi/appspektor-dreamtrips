package com.worldventures.dreamtrips.social.ui.tripsimages.service.storage;

import com.worldventures.core.janet.cache.CacheBundle;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ClearableStorage;
import com.worldventures.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetMemberMediaCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetUsersMediaCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.MemberImagesAddedCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.MemberImagesRemovedCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TripImageStorage implements MultipleActionStorage<List<BaseMediaEntity>>, ClearableStorage {
   public static final String PARAM_ARGS = "args";
   public static final String RELOAD = "RELOAD";
   public static final String LOAD_MORE = "LOAD_MORE";
   public static final String REMOVE_ITEMS = "REMOVE_ITEM";
   public static final String LOAD_LATEST = "LOAD_LATEST";

   private Map<TripImagesArgs, MemoryStorage<List<BaseMediaEntity>>> map = new ConcurrentHashMap<>();

   @Override
   public void clearMemory() {
      for (MemoryStorage<List<BaseMediaEntity>> memoryStorage : map.values()) {
         memoryStorage.clearMemory();
      }
      map.clear();
   }

   @Override
   public void save(@Nullable CacheBundle params, List<BaseMediaEntity> data) {
      TripImagesArgs args = params.get(PARAM_ARGS);
      MemoryStorage<List<BaseMediaEntity>> storage = map.get(args);
      if (storage == null) {
         storage = new MemoryStorage<>();
         map.put(args, storage);
      }

      if (params.get(RELOAD)) {
         storage.save(params, data);
      } else if (params.get(LOAD_MORE)) {
         List<BaseMediaEntity> cachedItems = storage.get(params);
         cachedItems.addAll(data);
         storage.save(params, cachedItems);
      } else if (params.get(LOAD_LATEST)) {
         List<BaseMediaEntity> cachedItems = storage.get(params);
         cachedItems.addAll(0, data);
         storage.save(params, cachedItems);
      } else if (params.get(REMOVE_ITEMS)) {
         List<BaseMediaEntity> cachedItems = storage.get(params);
         cachedItems.removeAll(data);
         storage.save(params, cachedItems);
      }
   }

   @Override
   public List<BaseMediaEntity> get(@Nullable CacheBundle params) {
      TripImagesArgs args = params.get(PARAM_ARGS);
      MemoryStorage<List<BaseMediaEntity>> storage = map.get(args);
      if (storage == null) {
         storage = new MemoryStorage<>();
         map.put(args, storage);
      }
      return storage.get(params);
   }

   @Override
   public List<Class<? extends CachedAction>> getActionClasses() {
      return Arrays.asList(GetMemberMediaCommand.class, GetUsersMediaCommand.class,
            MemberImagesAddedCommand.class, MemberImagesRemovedCommand.class);
   }
}
