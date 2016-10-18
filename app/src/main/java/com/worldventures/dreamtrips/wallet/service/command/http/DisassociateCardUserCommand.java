package com.worldventures.dreamtrips.wallet.service.command.http;

import com.worldventures.dreamtrips.api.smart_card.user_association.DisassociateCardUserHttpAction;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;

@CommandAction
public class DisassociateCardUserCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_API_LIB) Janet janet;

   private final String smartCardId;

   public DisassociateCardUserCommand(String barcode) {
      this.smartCardId = barcode;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      janet.createPipe(DisassociateCardUserHttpAction.class)
            .createObservableResult(new DisassociateCardUserHttpAction(Long.parseLong(smartCardId)))
            .subscribe(action -> callback.onSuccess(null), callback::onFail);
   }
}
