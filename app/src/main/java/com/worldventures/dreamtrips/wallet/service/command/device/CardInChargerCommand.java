package com.worldventures.dreamtrips.wallet.service.command.device;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.charger.CardInChargerAction;

@CommandAction
public class CardInChargerCommand extends Command<Boolean> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;

   @Override
   protected void run(CommandCallback<Boolean> callback) throws Throwable {
      janet.createPipe(CardInChargerAction.class)
            .createObservableResult(new CardInChargerAction())
            .map(cardInChargerAction -> cardInChargerAction.inCharger)
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
