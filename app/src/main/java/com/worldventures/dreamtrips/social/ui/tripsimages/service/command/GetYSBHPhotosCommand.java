package com.worldventures.dreamtrips.social.ui.tripsimages.service.command;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.janet.cache.CacheBundleImpl;
import com.worldventures.core.janet.cache.CacheOptions;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.ysbh.GetYSBHPhotosHttpAction;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.YSBHPhoto;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.storage.YsbhPhotoStorage;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetYSBHPhotosCommand extends CommandWithError<List<YSBHPhoto>> implements InjectableAction, CachedAction<List<YSBHPhoto>> {

   public static final int PER_PAGE = 40;

   @Inject Janet janet;
   @Inject MapperyContext mappery;

   protected int page;

   private boolean fromCache;
   private List<YSBHPhoto> cachedItems;

   public static GetYSBHPhotosCommand cachedCommand() {
      GetYSBHPhotosCommand getYSBHPhotosCommand = new GetYSBHPhotosCommand();
      getYSBHPhotosCommand.fromCache = true;
      return getYSBHPhotosCommand;
   }

   public static GetYSBHPhotosCommand commandForPage(int page) {
      GetYSBHPhotosCommand getYSBHPhotosCommand = new GetYSBHPhotosCommand();
      getYSBHPhotosCommand.page = page;
      return getYSBHPhotosCommand;
   }

   public boolean isFromCache() {
      return fromCache;
   }

   public int getPage() {
      return page;
   }

   public void setPage(int page) {
      this.page = page;
   }

   @Override
   protected void run(CommandCallback<List<YSBHPhoto>> callback) throws Throwable {
      if (fromCache && cachedItems != null) callback.onSuccess(cachedItems);
      else {
         janet.createPipe(GetYSBHPhotosHttpAction.class)
               .createObservableResult(new GetYSBHPhotosHttpAction(page, PER_PAGE))
               .map(GetYSBHPhotosHttpAction::response)
               .map(photos -> mappery.convert(photos, YSBHPhoto.class))
               .subscribe(callback::onSuccess, callback::onFail);
      }
   }

   public boolean lastPageReached() {
      return getResult().size() < PER_PAGE;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_load_member_images;
   }

   @Override
   public List<YSBHPhoto> getCacheData() {
      return new ArrayList<>(getResult());
   }

   @Override
   public void onRestore(ActionHolder holder, List<YSBHPhoto> cache) {
      cachedItems = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundleImpl cacheBundle = new CacheBundleImpl();
      cacheBundle.put(YsbhPhotoStorage.RELOAD, page == 1);
      cacheBundle.put(YsbhPhotoStorage.LOAD_MORE, page != 1);
      return ImmutableCacheOptions.builder().params(cacheBundle).build();
   }
}
