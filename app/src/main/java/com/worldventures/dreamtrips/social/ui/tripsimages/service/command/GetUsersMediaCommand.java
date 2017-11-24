package com.worldventures.dreamtrips.social.ui.tripsimages.service.command;

import com.worldventures.core.janet.cache.CacheBundleImpl;
import com.worldventures.core.janet.cache.CacheOptions;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.multimedia.GetUserMultimediaHttpAction;
import com.worldventures.dreamtrips.api.multimedia.ImmutableMultimediaPaginatedParams;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.storage.TripImageStorage;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetUsersMediaCommand extends BaseMediaCommand implements InjectableAction, CachedAction<List<BaseMediaEntity>> {

   @Inject Janet janet;
   @Inject MapperyContext mappery;

   private int userId;
   private int perPage;
   protected Date before;
   protected Date after;

   private boolean fromCache;
   private List<BaseMediaEntity> cachedItems;

   public GetUsersMediaCommand(TripImagesArgs args, GetMemberMediaCommand.PaginationParams paginationParams) {
      super(args);
      this.userId = args.getUserId();
      this.perPage = args.getPageSize();
      this.before = paginationParams.before();
      this.after = paginationParams.after();
   }

   public GetUsersMediaCommand(TripImagesArgs args, boolean fromCache) {
      super(args);
      this.fromCache = fromCache;
   }

   @Override
   protected void run(CommandCallback<List<BaseMediaEntity>> callback) throws Throwable {
      if (fromCache) {
         callback.onSuccess(cachedItems);
      } else {
         janet.createPipe(GetUserMultimediaHttpAction.class)
               .createObservableResult(new GetUserMultimediaHttpAction(userId, ImmutableMultimediaPaginatedParams.builder()
                     .before(before)
                     .after(after)
                     .pageSize(perPage).build()))
               .map(GetUserMultimediaHttpAction::response)
               .map(photos -> mappery.convert(photos, BaseMediaEntity.class))
               .subscribe(callback::onSuccess, callback::onFail);
      }
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
      cacheBundle.put(TripImageStorage.REMOVE_ITEMS, false);
      return ImmutableCacheOptions.builder()
            .params(cacheBundle)
            .saveToCache(!fromCache)
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
