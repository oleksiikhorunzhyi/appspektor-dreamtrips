package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.GetDefaultRecordAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class FetchDefaultCardIdCommand extends Command<String> implements InjectableAction, CachedAction<String> {
   @Inject @Named(JANET_WALLET) Janet janet;

   private String defaultCardId = null;

   private boolean force;

   public static FetchDefaultCardIdCommand fetch(boolean force) {
      return new FetchDefaultCardIdCommand(force);
   }

   private FetchDefaultCardIdCommand(boolean force) {
      this.force = force;
   }

   @Override
   protected void run(CommandCallback<String> callback) throws Throwable {
      Observable<String> defaultCard = defaultCardId == null || force ? deviceDefaultCard() : Observable.just(defaultCardId);
      defaultCard.subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public String getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, String cachedCardId) {
      defaultCardId = cachedCardId;
      force = (defaultCardId == null || force);
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .saveToCache(force)
            .build();
   }

   private Observable<String> deviceDefaultCard() {
      return janet.createPipe(GetDefaultRecordAction.class)
            .createObservableResult(new GetDefaultRecordAction())
            .map(it -> String.valueOf(it.recordId));
   }
}