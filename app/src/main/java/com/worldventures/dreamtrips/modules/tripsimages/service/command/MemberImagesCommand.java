package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.photos.GetPhotosHttpAction;
import com.worldventures.dreamtrips.api.photos.ImmutablePhotosPaginatedParams;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.service.storage.TripImageStorage;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.TripImagesArgs;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class MemberImagesCommand extends BaseTripImagesCommand implements InjectableAction, CachedAction<List<BaseMediaEntity>> {

   @Inject Janet janet;
   @Inject MapperyContext mappery;

   protected Date before;
   protected Date after;
   protected int perPage;

   private boolean fromCache;
   private List<BaseMediaEntity> cachedItems = new ArrayList<>();

   public MemberImagesCommand(TripImagesArgs args, PaginationParams paginationParams) {
      super(args);
      this.before = paginationParams.before();
      this.after = paginationParams.after();
      this.perPage = paginationParams.perPage();
   }

   public MemberImagesCommand(TripImagesArgs args, boolean fromCache) {
      super(args);
      this.fromCache = fromCache;
   }

   @Override
   protected void run(CommandCallback<List<BaseMediaEntity>> callback) throws Throwable {
      if (fromCache) callback.onSuccess(cachedItems);
      else {
         janet.createPipe(GetPhotosHttpAction.class)
               .createObservableResult(new GetPhotosHttpAction(ImmutablePhotosPaginatedParams.builder()
                     .before(before)
                     .after(after)
                     .pageSize(perPage).build()))
               .map(GetPhotosHttpAction::response)
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
      cachedItems = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundleImpl cacheBundle = new CacheBundleImpl();
      cacheBundle.put(TripImageStorage.PARAM_ARGS, getArgs());
      cacheBundle.put(TripImageStorage.LOAD_MORE, isLoadMore());
      cacheBundle.put(TripImageStorage.RELOAD, isReload());
      cacheBundle.put(TripImageStorage.LOAD_LATEST, false);
      return ImmutableCacheOptions.builder()
            .params(cacheBundle)
            .build();
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_load_member_images;
   }

   @Override
   public boolean lastPageReached() {
      return getResult().size() < perPage;
   }

   @Value.Immutable
   public interface PaginationParams {

      int perPage();

      @Nullable
      Date before();

      @Nullable
      Date after();
   }

}
