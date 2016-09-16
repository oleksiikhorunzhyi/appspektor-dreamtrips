package com.worldventures.dreamtrips.wallet.service.command.http;

import com.worldventures.dreamtrips.api.smart_card.terms_and_condition.GetTermsAndConditionsHttpAction;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableTermsAndConditions;
import com.worldventures.dreamtrips.wallet.domain.entity.TermsAndConditions;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;

@CommandAction
public class FetchTermsAndConditionsCommand extends Command<TermsAndConditions> implements InjectableAction, CachedAction<TermsAndConditions> {

   @Inject @Named(JANET_API_LIB) Janet janet;

   @Override
   protected void run(CommandCallback<TermsAndConditions> callback) throws Throwable {
      janet.createPipe(GetTermsAndConditionsHttpAction.class, Schedulers.io())
            .createObservableResult(new GetTermsAndConditionsHttpAction())
            .map(action -> convertResponse(action.response()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private TermsAndConditions convertResponse(com.worldventures.dreamtrips.api.smart_card.terms_and_condition.model.TermsAndConditions response) {
      return ImmutableTermsAndConditions.builder()
            .tacVersion(String.valueOf(response.version()))
            .url(response.url())
            .build();
   }

   @Override
   public TermsAndConditions getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, TermsAndConditions cache) {

   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .restoreFromCache(false)
            .saveToCache(true)
            .build();
   }

}
