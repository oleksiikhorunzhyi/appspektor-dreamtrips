package com.worldventures.dreamtrips.wallet.service.command;

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
import io.techery.janet.helper.ActionStateToActionTransformer;
import io.techery.janet.smartcard.action.settings.SetStealthModeAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class SetStealthModeCommand extends Command<SmartCard> implements InjectableAction, SmartCardModifier, CachedAction<SmartCard> {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SmartCardInteractor smartCardInteractor;

   public final boolean stealthModeEnabled;
   public SmartCard smartCard;

   public SetStealthModeCommand(boolean stealthModeEnabled) {
      this.stealthModeEnabled = stealthModeEnabled;
   }

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      janet.createPipe(SetStealthModeAction.class)
            .createObservableResult(new SetStealthModeAction(stealthModeEnabled))
            .flatMap(action -> fetchActiveSmartCard())
            .map(smartCard -> ImmutableSmartCard.builder().from(smartCard).stealthMode(stealthModeEnabled).build())
            .doOnNext(smartCard -> this.smartCard = smartCard)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<SmartCard> fetchActiveSmartCard() {
      return smartCardInteractor.activeSmartCardPipe()
            .createObservable(new GetActiveSmartCardCommand())
            .compose(new ActionStateToActionTransformer<>())
            .map(Command::getResult);
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
            .saveToCache(true)
            .build();
   }
}
