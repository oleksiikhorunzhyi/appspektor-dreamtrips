package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class SaveLockStateCommand extends Command<SmartCard> implements InjectableAction, SmartCardModifier, CachedAction<SmartCard> {

   @Inject @Named(JANET_WALLET) Janet janet;

   private final boolean lock;

   public SaveLockStateCommand(boolean lock) {
      this.lock = lock;
   }

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      fetchActiveSmartCard()
            .map(smartCard -> ImmutableSmartCard.builder()
                  .from(smartCard)
                  .lock(lock)
                  .build())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<SmartCard> fetchActiveSmartCard() {
      return janet.createPipe(GetActiveSmartCardCommand.class)
            .createObservableResult(new GetActiveSmartCardCommand())
            .map(Command::getResult);
   }

   public boolean isLock() {
      return lock;
   }

   @Override
   public SmartCard getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, SmartCard cache) {
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .restoreFromCache(false)
            .saveToCache(true)
            .build();
   }
}
