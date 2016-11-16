package com.worldventures.dreamtrips.wallet.service.command.http;

import com.worldventures.dreamtrips.api.smart_card.user_association.DisassociateCardUserHttpAction;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.SystemPropertiesProvider;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.DisconnectAction;
import rx.Observable;

@CommandAction
public class DisassociateCardUserCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet apiJanet;
   @Inject @Named(JanetModule.JANET_WALLET) Janet walletJanet;
   @Inject SystemPropertiesProvider propertiesProvider;

   private final String smartCardId;

   public DisassociateCardUserCommand(String smartCardId) {
      this.smartCardId = smartCardId;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      Observable.zip(
            walletJanet.createPipe(DisconnectAction.class)
                  .createObservableResult(new DisconnectAction())
                  .onErrorResumeNext(Observable.just(null)),
            apiJanet.createPipe(DisassociateCardUserHttpAction.class)
                  .createObservableResult(new DisassociateCardUserHttpAction(Long.parseLong(smartCardId), propertiesProvider.deviceId()))
                  .onErrorResumeNext(Observable.just(null)),
            (o1, o2) -> null
      ).subscribe((result) -> {
         callback.onSuccess(null);
      }, callback::onFail);
   }
}
