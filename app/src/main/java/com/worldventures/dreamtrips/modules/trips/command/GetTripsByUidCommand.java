package com.worldventures.dreamtrips.modules.trips.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.trip.GetTripsDetailsHttpAction;
import com.worldventures.dreamtrips.api.trip.model.TripWithoutDetails;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.CommandActionBaseHelper;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.storage.TripsByUidsStorage;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetTripsByUidCommand extends CommandWithError<List<TripModel>> implements InjectableAction,
      CachedAction<List<TripModel>> {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mappery;

   private List<String> tripUids;

   private List<TripModel> cachedTrips;

   public GetTripsByUidCommand(List<String> tripUids) {
      this.tripUids = tripUids;
   }

   @Override
   protected void run(CommandCallback<List<TripModel>> callback) throws Throwable {
      if (hasValidCachedItems()) callback.onProgress(0);
      //
      janet.createPipe(GetTripsDetailsHttpAction.class)
            .createObservableResult(new GetTripsDetailsHttpAction(tripUids))
            .map(GetTripsDetailsHttpAction::response)
            .map(this::mapItems)
            .subscribe(CommandActionBaseHelper.ActionCommandSubscriber.wrap(callback));
   }

   public boolean hasValidCachedItems() {
      return cachedTrips != null && cachedTrips.size() > 0 && requestedSizeEqualsCached();
   }

   private boolean requestedSizeEqualsCached() {
      return cachedTrips.size() == tripUids.size();
   }

   private List<TripModel> mapItems(List<TripWithoutDetails> apiTrips) {
      return mappery.convert(apiTrips, TripModel.class);
   }

   public List<TripModel> getItems() {
      if (getResult() != null) return getResult();
      else return cachedTrips;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.string_failed_to_load_trips;
   }

   @Override
   public List<TripModel> getCacheData() {
      return new ArrayList<>(getResult());
   }

   @Override
   public void onRestore(ActionHolder holder, List<TripModel> cache) {
      cachedTrips = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundle cacheBundle = new CacheBundleImpl();
      cacheBundle.put(TripsByUidsStorage.UIDS, tripUids);
      return ImmutableCacheOptions.builder().params(cacheBundle).build();
   }
}
