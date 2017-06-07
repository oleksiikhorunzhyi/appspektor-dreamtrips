package com.worldventures.dreamtrips.modules.trips.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.trip.GetTripsLocationsHttpAction;
import com.worldventures.dreamtrips.api.trip.ImmutableGetTripsLocationsHttpAction;
import com.worldventures.dreamtrips.api.trip.model.TripPinWrapper;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.CommandActionBaseHelper;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.trips.model.Pin;
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
public class GetTripsLocationsCommand extends CommandWithError<List<Pin>> implements InjectableAction,
      CachedAction<List<Pin>> {

   @Inject Janet janet;
   @Inject MapperyContext mappery;

   private String searchQuery;
   private TripsFilterData tripsFilterData;

   private List<Pin> cachedResult;

   public GetTripsLocationsCommand(String searchQuery, TripsFilterData tripsFilterData) {
      this.searchQuery = searchQuery;
      this.tripsFilterData = tripsFilterData;
   }

   @Override
   protected void run(CommandCallback<List<Pin>> callback) throws Throwable {
      if (cachedResult != null && !cachedResult.isEmpty()) callback.onProgress(0);
      //
      janet.createPipe(GetTripsLocationsHttpAction.class)
            .createObservableResult(new GetTripsLocationsHttpAction(getParams()))
            .map(GetTripsLocationsHttpAction::response)
            .map(this::convert)
            .subscribe(CommandActionBaseHelper.ActionCommandSubscriber.wrap(callback));
   }

   public List<Pin> convert(List<TripPinWrapper> tripPins) {
      return mappery.convert(tripPins, Pin.class);
   }

   private GetTripsLocationsHttpAction.Params getParams() {
      if (tripsFilterData == null) return ImmutableGetTripsLocationsHttpAction.Params.builder().build();
      return ImmutableGetTripsLocationsHttpAction.Params.builder()
            .durationMax(tripsFilterData.getMaxNights())
            .durationMin(tripsFilterData.getMinNights())
            .priceMin(tripsFilterData.getMinPrice())
            .priceMax(tripsFilterData.getMaxPrice())
            .liked(tripsFilterData.isShowFavorites())
            .soldOut(tripsFilterData.isShowSoldOut())
            .recentFirst(tripsFilterData.isShowRecentlyAdded())
            .endDate(tripsFilterData.getEndDate())
            .startDate(tripsFilterData.getStartDate())
            .query(searchQuery)
            .activities(tripsFilterData.getAcceptedActivities())
            .regions(tripsFilterData.getAcceptedRegions())
            .build();
   }

   public List<Pin> getItems() {
      if (getResult() != null) return getResult();
      else return cachedResult;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.string_failed_to_load_trips;
   }

   @Override
   public void onRestore(ActionHolder holder, List<Pin> cache) {
      cachedResult = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder().build();
   }

   @Override
   public List<Pin> getCacheData() {
      return new ArrayList<>(getResult());
   }
}
