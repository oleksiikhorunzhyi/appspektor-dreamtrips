package com.worldventures.dreamtrips.modules.common.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.janet.CommandWithError;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.command.GetActivitiesCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetRegionsCommand;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;
import com.worldventures.dreamtrips.modules.trips.model.filter.CachedTripFilters;
import com.worldventures.janet.cache.CacheOptions;
import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.injection.InjectableAction;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;

@CommandAction
public class TripsFilterDataCommand extends CommandWithError<CachedTripFilters>
      implements InjectableAction, CachedAction<CachedTripFilters> {

   @Inject Janet janet;

   private CachedTripFilters cachedTripFilters;

   @Override
   protected void run(CommandCallback<CachedTripFilters> callback) throws Throwable {
      if (cachedTripFilters != null) {
         callback.onProgress(0);
      }

      ActionPipe<GetActivitiesCommand> activitiesActionPipe = janet.createPipe(GetActivitiesCommand.class, Schedulers.io());
      ActionPipe<GetRegionsCommand> regionsActionPipe = janet.createPipe(GetRegionsCommand.class, Schedulers.io());

      Observable<List<ActivityModel>> activitiesObservable = activitiesActionPipe.createObservableResult(new GetActivitiesCommand())
            .map(GetActivitiesCommand::getResult);
      Observable<List<RegionModel>> regionsObservable = regionsActionPipe.createObservableResult(new GetRegionsCommand())
            .map(GetRegionsCommand::getResult);

      Observable.zip(regionsObservable, activitiesObservable, (regionModels, activityModels) -> {
         cachedTripFilters = new CachedTripFilters(regionModels, activityModels);
         return cachedTripFilters;
      }).subscribe(callback::onSuccess, callback::onFail);
   }

   public List<RegionModel> getRegions() {
      return new ArrayList<>(cachedTripFilters.getRegions());
   }

   public List<ActivityModel> getParentActivities() {
      return Queryable.from(cachedTripFilters.getActivities()).filter(ActivityModel::isParent).toList();
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_load_activities;
   }

   @Override
   public CachedTripFilters getCacheData() {
      return cachedTripFilters;
   }

   @Override
   public void onRestore(ActionHolder holder, CachedTripFilters cache) {
      this.cachedTripFilters = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return new CacheOptions();
   }
}
