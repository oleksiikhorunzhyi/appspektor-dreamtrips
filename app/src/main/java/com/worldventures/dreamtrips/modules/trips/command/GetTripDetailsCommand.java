package com.worldventures.dreamtrips.modules.trips.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.trip.GetTripHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.CommandActionBaseHelper;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.storage.TripDetailsStorage;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetTripDetailsCommand extends CommandWithError<TripModel> implements InjectableAction,
      CachedAction<TripModel> {

   @Inject Janet janet;
   @Inject MapperyContext mappery;
   @Inject SnappyRepository db;

   private String uid;

   private TripModel cachedModel;

   public GetTripDetailsCommand(String uid) {
      this.uid = uid;
   }

   @Override
   protected void run(CommandCallback<TripModel> callback) throws Throwable {
      if (cachedModel != null) callback.onProgress(0);
      janet.createPipe(GetTripHttpAction.class)
            .createObservableResult(new GetTripHttpAction(uid))
            .map(GetTripHttpAction::response)
            .map(tripWithDetails -> mappery.convert(tripWithDetails, TripModel.class))
            .subscribe(CommandActionBaseHelper.ActionCommandSubscriber.wrap(callback));
   }

   public TripModel getCachedModel() {
      return cachedModel;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_item_details;
   }

   @Override
   public TripModel getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, TripModel cache) {
      cachedModel = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundle cacheBundle = new CacheBundleImpl();
      cacheBundle.put(TripDetailsStorage.UID, uid);
      return ImmutableCacheOptions.builder().params(cacheBundle).build();
   }
}
