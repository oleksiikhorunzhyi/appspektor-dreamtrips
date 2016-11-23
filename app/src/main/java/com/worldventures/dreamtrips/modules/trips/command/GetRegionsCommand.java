package com.worldventures.dreamtrips.modules.trips.command;

import com.worldventures.dreamtrips.api.trip.GetTripRegionsHttpAction;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;

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
public class GetRegionsCommand extends Command<List<RegionModel>> implements InjectableAction, CachedAction<List<RegionModel>> {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mapperyContext;

   private List<RegionModel> cachedData;

   public GetRegionsCommand() {
   }

   @Override
   public List<RegionModel> getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, List<RegionModel> cache) {
      cachedData = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder().build();
   }

   @Override
   protected void run(CommandCallback<List<RegionModel>> callback) throws Throwable {
      if (cachedData == null || cachedData.size() == 0) {
         janet.createPipe(GetTripRegionsHttpAction.class, Schedulers.io())
               .createObservableResult(new GetTripRegionsHttpAction())
               .map(action -> mapperyContext.convert(action.response(), RegionModel.class))
               .subscribe(callback::onSuccess, callback::onFail);
      } else {
         callback.onSuccess(cachedData);
      }
   }
}
