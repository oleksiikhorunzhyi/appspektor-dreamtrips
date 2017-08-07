package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.photos.GetPhotosOfUserHttpAction;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.service.storage.TripImageStorage;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.TripImagesArgs;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class UserImagesCommand extends BaseTripImagesCommand implements InjectableAction, CachedAction<List<BaseMediaEntity>> {

   @Inject Janet janet;
   @Inject MapperyContext mappery;

   private int userId;
   private int perPage;
   private int page;

   private boolean fromCache;
   private List<BaseMediaEntity> cachedItems;

   public UserImagesCommand(TripImagesArgs args, int page) {
      super(args);
      this.userId = args.getUserId();
      this.perPage = args.getPageSize();
      this.page = page;
   }

   public UserImagesCommand(TripImagesArgs args, boolean fromCache) {
      super(args);
      this.fromCache = fromCache;
   }

   @Override
   protected void run(CommandCallback<List<BaseMediaEntity>> callback) throws Throwable {
      if (fromCache) callback.onSuccess(cachedItems);
      else {
         janet.createPipe(GetPhotosOfUserHttpAction.class)
               .createObservableResult(new GetPhotosOfUserHttpAction(userId, page, perPage))
               .map(GetPhotosOfUserHttpAction::response)
               .map(photos -> mappery.convert(photos, Photo.class))
               .map(this::mapItems)
               .subscribe(callback::onSuccess, callback::onFail);
      }
   }

   private List<BaseMediaEntity> mapItems(List<Photo> photos) {
      return Queryable.from(photos)
            .map(Photo::castToMediaEntity)
            .toList();
   }

   @Override
   public List<BaseMediaEntity> getCacheData() {
      return new ArrayList<>(getResult());
   }

   @Override
   public void onRestore(ActionHolder holder, List<BaseMediaEntity> cache) {
      this.cachedItems = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundleImpl cacheBundle = new CacheBundleImpl();
      cacheBundle.put(TripImageStorage.PARAM_ARGS, getArgs());
      cacheBundle.put(TripImageStorage.RELOAD, isReload());
      cacheBundle.put(TripImageStorage.LOAD_MORE, isLoadMore());
      cacheBundle.put(TripImageStorage.LOAD_LATEST, false);
      return ImmutableCacheOptions.builder()
            .params(cacheBundle)
            .build();
   }

   @Override
   public boolean lastPageReached() {
      return getResult().size() < perPage;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_load_member_images;
   }
}
