package com.worldventures.dreamtrips.modules.trips.command;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.api.janet.GetRegionsHttpAction;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class GetRegionsCommand extends Command<List<RegionModel>> implements InjectableAction, CachedAction<List<RegionModel>> {

   @Inject Janet janet;

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
         janet.createPipe(GetRegionsHttpAction.class, Schedulers.io())
               .createObservableResult(new GetRegionsHttpAction())
               .map(GetRegionsHttpAction::getRegionModels)
               .subscribe(callback::onSuccess, callback::onFail);
      } else {
         callback.onSuccess(cachedData);
      }
   }
}
