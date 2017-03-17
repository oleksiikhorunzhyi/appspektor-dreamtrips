package com.worldventures.dreamtrips.modules.trips.command;

import com.worldventures.dreamtrips.api.trip.GetTripActivitiesHttpAction;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.schedulers.Schedulers;

@CommandAction
public class GetActivitiesCommand extends Command<List<ActivityModel>> implements InjectableAction, CachedAction<List<ActivityModel>> {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;

   List<ActivityModel> cachedResult;

   @Override
   public List<ActivityModel> getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, List<ActivityModel> cache) {
      cachedResult = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder().build();
   }

   @Override
   protected void run(CommandCallback<List<ActivityModel>> callback) throws Throwable {
      if (cachedResult == null || cachedResult.size() == 0) {
         janet.createPipe(GetTripActivitiesHttpAction.class, Schedulers.io())
               .createObservableResult(new GetTripActivitiesHttpAction())
               .map(action -> mapperyContext.convert(action.response(), ActivityModel.class))
               .subscribe(callback::onSuccess, callback::onFail);
      } else {
         callback.onSuccess(cachedResult);
      }
   }
}
