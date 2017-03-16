package com.worldventures.dreamtrips.modules.flags.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.flagging.GetFlagReasonsHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

@CommandAction
public class GetFlagsCommand extends CommandWithError<List<Flag>> implements InjectableAction, CachedAction<List<Flag>> {

   @Inject Janet janet;
   @Inject MapperyContext mappery;

   private List<Flag> cachedFlags;

   @Override
   protected void run(CommandCallback<List<Flag>> callback) throws Throwable {
      if (cachedFlags != null) {
         callback.onSuccess(cachedFlags);
         return;
      }
      janet.createPipe(GetFlagReasonsHttpAction.class)
            .createObservableResult(new GetFlagReasonsHttpAction())
            .map(GetFlagReasonsHttpAction::getFlagReasons)
            .flatMap(Observable::from)
            .map(flags -> mappery.convert(flags, Flag.class))
            .toList()
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public List<Flag> getCacheData() {
      return new ArrayList<>(getResult());
   }

   @Override
   public void onRestore(ActionHolder holder, List<Flag> cache) {
      cachedFlags = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder().build();
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_flag_reason;
   }
}
