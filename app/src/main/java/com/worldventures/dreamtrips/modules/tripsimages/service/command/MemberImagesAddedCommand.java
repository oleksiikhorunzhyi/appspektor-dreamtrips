package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.modules.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.service.storage.TripImageStorage;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.TripImagesArgs;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class MemberImagesAddedCommand extends Command<List<BaseMediaEntity>> implements CachedAction<List<BaseMediaEntity>> {

   TripImagesArgs tripImagesArgs;
   List<BaseMediaEntity> baseMediaEntities;

   public MemberImagesAddedCommand(TripImagesArgs tripImagesArgs, List<BaseMediaEntity> baseMediaEntities) {
      this.tripImagesArgs = tripImagesArgs;
      this.baseMediaEntities = baseMediaEntities;
   }

   @Override
   protected void run(CommandCallback<List<BaseMediaEntity>> callback) throws Throwable {
      callback.onSuccess(baseMediaEntities);
   }

   @Override
   public List<BaseMediaEntity> getCacheData() {
      return new ArrayList<>(getResult());
   }

   @Override
   public void onRestore(ActionHolder holder, List<BaseMediaEntity> cache) {}

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundleImpl cacheBundle = new CacheBundleImpl();
      cacheBundle.put(TripImageStorage.PARAM_ARGS, tripImagesArgs);
      cacheBundle.put(TripImageStorage.LOAD_LATEST, true);
      return ImmutableCacheOptions.builder()
            .restoreFromCache(false)
            .build();
   }
}
