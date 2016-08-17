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
import io.techery.janet.smartcard.action.records.SetRecordAsDefaultAction;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class SetDefaultCardOnDeviceCommand extends Command<String> implements InjectableAction, CachedAction<String> {

   @Inject @Named(JANET_WALLET) Janet janet;

   final String cardId;

   public SetDefaultCardOnDeviceCommand(String cardId) {
      this.cardId = cardId;
   }

   @Override
   protected void run(CommandCallback<String> callback) throws Throwable {
      janet.createPipe(SetRecordAsDefaultAction.class, Schedulers.io())
            .createObservableResult(new SetRecordAsDefaultAction(Integer.parseInt(cardId)))
            .subscribe(action -> callback.onSuccess(String.valueOf(action.recordId)), callback::onFail);
   }

   @Override
   public String getCacheData() {
      return cardId;
   }

   @Override
   public void onRestore(ActionHolder holder, String cache) {
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder().restoreFromCache(false).build();
   }

}
