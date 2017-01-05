package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.SetStealthModeAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class SetStealthModeCommand extends Command<Boolean> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;

   public final boolean stealthModeEnabled;

   public SetStealthModeCommand(boolean stealthModeEnabled) {
      this.stealthModeEnabled = stealthModeEnabled;
   }

   @Override
   protected void run(CommandCallback<Boolean> callback) throws Throwable {
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
            )
            .map(smartCard -> stealthModeEnabled)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<SmartCard> fetchSmartCard() {
      return janet.createPipe(ActiveSmartCardCommand.class)
            .createObservableResult(new ActiveSmartCardCommand())
            .map(Command::getResult);
   }
}
