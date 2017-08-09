package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.inspirations.GetInspireMePhotosHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.service.storage.InspireMeStorage;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetInspireMePhotosCommand extends CommandWithError<List<Inspiration>> implements InjectableAction, CachedAction<List<Inspiration>> {

   public static final int PER_PAGE = 40;

   @Inject Janet janet;
   @Inject MapperyContext mappery;

   protected int page;
   private double randomSeed; // used later when API lib is fixed for inspire me photos

   private boolean fromCache;
   private List<Inspiration> cachedItems;

   public static GetInspireMePhotosCommand cachedCommand() {
      GetInspireMePhotosCommand command = new GetInspireMePhotosCommand();
      command.fromCache = true;
      return command;
   }

   public static GetInspireMePhotosCommand forPage(double randomSeed, int page) {
      GetInspireMePhotosCommand command = new GetInspireMePhotosCommand();
      command.page = page;
      command.randomSeed = randomSeed;
      return command;
   }


   @Override
   protected void run(CommandCallback<List<Inspiration>> callback) throws Throwable {
      if (fromCache && cachedItems != null) callback.onSuccess(cachedItems);
      else {
         janet.createPipe(GetInspireMePhotosHttpAction.class)
               .createObservableResult(new GetInspireMePhotosHttpAction(randomSeed, page, PER_PAGE))
               .map(GetInspireMePhotosHttpAction::response)
               .map(inspireMePhotos -> mappery.convert(inspireMePhotos, Inspiration.class))
               .subscribe(callback::onSuccess, callback::onFail);
      }
   }

   public int getPage() {
      return page;
   }

   public boolean lastPageReached() {
      return getResult().size() < PER_PAGE;
   }

   @Override
   public List<Inspiration> getCacheData() {
      return new ArrayList<>(getResult());
   }

   @Override
   public void onRestore(ActionHolder holder, List<Inspiration> cache) {
      cachedItems = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundleImpl cacheBundle = new CacheBundleImpl();
      cacheBundle.put(InspireMeStorage.RELOAD, page == 1);
      cacheBundle.put(InspireMeStorage.LOAD_MORE, page != 1);
      return ImmutableCacheOptions.builder().params(cacheBundle).build();
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_load_inspire_images;
   }
}
