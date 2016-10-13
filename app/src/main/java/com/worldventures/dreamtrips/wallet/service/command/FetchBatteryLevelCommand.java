package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.GetBatteryLevelAction;
import rx.Observable;

@CommandAction
public class FetchBatteryLevelCommand extends Command<SmartCard> implements SmartCardModifier, InjectableAction, CachedAction<SmartCard> {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
   @Inject SmartCardInteractor smartCardInteractor;

   private SmartCard smartCard;

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      janet.createPipe(GetBatteryLevelAction.class)
            .createObservableResult(new GetBatteryLevelAction())
            .map(action -> action.level)
            .onErrorReturn(throwable -> "0")
            .flatMap(butteryLevel -> updateSmartCard(Integer.parseInt(butteryLevel)))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<SmartCard> updateSmartCard(int butteryLevel) {
      return smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new GetActiveSmartCardCommand())
            .map(command -> smartCard = ImmutableSmartCard.builder()
                  .from(command.getResult())
                  .batteryLevel(butteryLevel)
                  .build()
            );
   }

   @Override
   public SmartCard getCacheData() {
      return smartCard;
   }

   @Override
   public void onRestore(ActionHolder holder, SmartCard cache) {
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .restoreFromCache(false)
            .build();
   }
}
