package com.worldventures.dreamtrips.modules.infopages.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.feedback.GetFeedbackReasonsHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetFeedbackCommand extends CommandWithError<List<FeedbackType>> implements InjectableAction,
      CachedAction<List<FeedbackType>> {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mappery;

   private List<FeedbackType> cachedItems;

   @Override
   protected void run(CommandCallback<List<FeedbackType>> callback) throws Throwable {
      janet.createPipe(GetFeedbackReasonsHttpAction.class)
            .createObservableResult(new GetFeedbackReasonsHttpAction())
            .map(action -> mappery.convert(action.reasons(), FeedbackType.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   public List<FeedbackType> items() {
      if (getResult() != null) return getResult();
      else return cachedItems;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_feedback_reasons;
   }

   @Override
   public List<FeedbackType> getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, List<FeedbackType> cache) {
      cachedItems = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder().build();
   }
}
