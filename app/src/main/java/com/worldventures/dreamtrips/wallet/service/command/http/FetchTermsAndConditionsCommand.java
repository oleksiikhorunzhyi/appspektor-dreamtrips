package com.worldventures.dreamtrips.wallet.service.command.http;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableTermsAndConditionsResponse;
import com.worldventures.dreamtrips.wallet.domain.entity.TermsAndConditionsResponse;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class FetchTermsAndConditionsCommand extends Command<TermsAndConditionsResponse> implements InjectableAction, CachedAction<TermsAndConditionsResponse> {

   @Inject @Named(JANET_WALLET) Janet janet;


   @Override
   protected void run(CommandCallback<TermsAndConditionsResponse> callback) throws Throwable {
      Observable.just(getMockResponse())
            .subscribe(callback::onSuccess, callback::onFail);
      //todo use real route for get T&C from API library]..
   }

   @Override
   public TermsAndConditionsResponse getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, TermsAndConditionsResponse cache) {

   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .restoreFromCache(false)
            .saveToCache(true)
            .build();
   }

   private ImmutableTermsAndConditionsResponse getMockResponse() {
      return ImmutableTermsAndConditionsResponse.builder()
            .tacVersion("random")
            .url("http://assets.wvholdings.com/1/dtapp/legal/us_en/html/terms_of_service07112016.html")
            .build();
   }
}
