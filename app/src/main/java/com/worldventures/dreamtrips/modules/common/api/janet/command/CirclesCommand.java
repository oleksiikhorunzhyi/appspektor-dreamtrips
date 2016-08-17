package com.worldventures.dreamtrips.modules.common.api.janet.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.api.janet.GetCirclesHttpAction;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class CirclesCommand extends CommandWithError<List<Circle>> implements InjectableAction, CachedAction<List<Circle>> {

   @Inject Janet janet;

   private List<Circle> cachedData;

   public CirclesCommand() {
   }

   @Override
   protected void run(CommandCallback<List<Circle>> callback) throws Throwable {
      if (cachedData == null || cachedData.isEmpty()) {
         janet.createPipe(GetCirclesHttpAction.class, Schedulers.io())
               .createObservableResult(new GetCirclesHttpAction())
               .map(getCirclesHttpAction -> {
                  ArrayList<Circle> circles = getCirclesHttpAction.getCircles();
                  if (circles == null) circles = new ArrayList<>();
                  return circles;
               })
               .subscribe(callback::onSuccess, callback::onFail);
      } else {
         callback.onSuccess(cachedData);
      }
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_circles;
   }

   @Override
   public List<Circle> getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, List<Circle> cache) {
      cachedData = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder().build();
   }
}
