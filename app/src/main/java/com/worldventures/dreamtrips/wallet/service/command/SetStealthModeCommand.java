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
import io.techery.janet.smartcard.action.settings.SetStealthModeAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class SetStealthModeCommand extends Command<SmartCard> implements InjectableAction, SmartCardModifier, CachedAction<SmartCard> {

   @Inject @Named(JANET_WALLET) Janet janet;

   public final boolean stealthModeEnabled;

   public SetStealthModeCommand(boolean stealthModeEnabled) {
      this.stealthModeEnabled = stealthModeEnabled;
   }

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      fetchSmartCard()
            .flatMap(smartCard -> {
               if (smartCard.stealthMode() == stealthModeEnabled) {
                  return Observable.error(new IllegalArgumentException("Stealth mode already turned " + (stealthModeEnabled ? "on" : "off")));
               } else {
                  return Observable.just(smartCard);
               }
            })
            .flatMap(smartCard -> janet.createPipe(SetStealthModeAction.class)
                  .createObservableResult(new SetStealthModeAction(stealthModeEnabled))
                  .flatMap(action -> updateSmartCard())
            )
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<SmartCard> fetchSmartCard() {
      return janet.createPipe(GetActiveSmartCardCommand.class)
            .createObservableResult(new GetActiveSmartCardCommand())
            .map(Command::getResult);
   }

   private Observable<SmartCard> updateSmartCard() {
      return fetchSmartCard()
            .map(smartCard -> ImmutableSmartCard.builder()
                  .from(smartCard)
                  .stealthMode(stealthModeEnabled)
                  .build());
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
