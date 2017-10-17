package com.worldventures.dreamtrips.social.ui.friends.service.command;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.janet.cache.CacheOptions;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.core.model.Circle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.circles.GetCirclesHttpAction;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.schedulers.Schedulers;

@CommandAction
public class GetCirclesCommand extends CommandWithError<List<Circle>> implements InjectableAction, CachedAction<List<Circle>> {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;

   private List<Circle> cachedData;

   @Override
   protected void run(CommandCallback<List<Circle>> callback) throws Throwable {
      if (cachedData == null || cachedData.isEmpty()) {
         janet.createPipe(GetCirclesHttpAction.class, Schedulers.io())
               .createObservableResult(new GetCirclesHttpAction())
               .map(action -> mapperyContext.convert(action.response(), Circle.class))
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
      return new ArrayList<>(getResult());
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
