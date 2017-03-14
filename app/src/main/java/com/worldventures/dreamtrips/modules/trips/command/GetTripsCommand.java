package com.worldventures.dreamtrips.modules.trips.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.trip.GetTripsHttpAction;
import com.worldventures.dreamtrips.api.trip.ImmutableGetTripsHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.CommandActionBaseHelper.ActionCommandSubscriber;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedStorage;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.util.TripsFilterData;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetTripsCommand extends CommandWithError<List<TripModel>> implements InjectableAction,
      CachedAction<List<TripModel>> {

   public static final int PER_PAGE = 20;

   @Inject Janet janet;
   @Inject MapperyContext mappery;

   private String searchQuery;
   private TripsFilterData tripsFilterData;

   private boolean refresh;

   private List<TripModel> cachedData;

   public GetTripsCommand(String searchQuery, TripsFilterData tripsFilterData, boolean refresh) {
      this.searchQuery = searchQuery;
      this.tripsFilterData = tripsFilterData;
      this.refresh = refresh;
   }

   @Override
   protected void run(CommandCallback<List<TripModel>> callback) throws Throwable {
      if (cachedData != null && !cachedData.isEmpty()) callback.onProgress(0);
      //
      janet.createPipe(GetTripsHttpAction.class)
            .createObservableResult(new GetTripsHttpAction(params(tripsFilterData, searchQuery, getPage())))
            .map(GetTripsHttpAction::response)
            .map(trips -> mappery.convert(trips, TripModel.class))
            .doOnNext(trips -> clearCacheIfNeeded())
            .subscribe(ActionCommandSubscriber.wrap(callback));
   }

   private void clearCacheIfNeeded() {
      if (refresh) cachedData = null;
   }

   private int getPage() {
      if (refresh || cachedData == null || cachedData.isEmpty()) {
         return 1;
      }
      return cachedData.size() / PER_PAGE + 1;
   }

   private ImmutableGetTripsHttpAction.Params params(TripsFilterData tripsFilterData, String query, int page) {
      ImmutableGetTripsHttpAction.Params.Builder params = ImmutableGetTripsHttpAction.Params.builder()
            .page(page)
            .perPage(PER_PAGE)
            .query(query);
      if (tripsFilterData != null) {
         params.durationMin(tripsFilterData.getMinNights())
               .durationMax(tripsFilterData.getMaxNights())
               .priceMin(tripsFilterData.getMinPrice())
               .priceMax(tripsFilterData.getMaxPrice())
               .startDate(tripsFilterData.getStartDate())
               .endDate(tripsFilterData.getEndDate())
               .regions(tripsFilterData.getAcceptedRegions())
               .activities(tripsFilterData.getAcceptedActivities())
               .soldOut(tripsFilterData.isShowSoldOut())
               .recentFirst(tripsFilterData.isShowRecentlyAdded())
               .liked(tripsFilterData.isShowFavorites());
      }
      return params.build();
   }

   public boolean hasMore() {
      return getResult().size() == PER_PAGE;
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
      cachedData = new ArrayList<>(cache);
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundle cacheBundle = new CacheBundleImpl();
      cacheBundle.put(PaginatedStorage.BUNDLE_REFRESH, refresh);
      return ImmutableCacheOptions.builder().params(cacheBundle).build();
   }

   public List<TripModel> getItems() {
      List<TripModel> trips = new ArrayList<>();
      if (cachedData != null) trips.addAll(cachedData);
      if (getResult() != null) trips.addAll(getResult());
      return trips;
   }

}
