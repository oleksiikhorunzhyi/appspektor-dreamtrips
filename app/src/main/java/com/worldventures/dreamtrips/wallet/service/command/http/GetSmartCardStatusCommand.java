package com.worldventures.dreamtrips.wallet.service.command.http;

import com.worldventures.dreamtrips.api.smart_card.status.SmartCardStatusHttpAction;
import com.worldventures.dreamtrips.api.smart_card.status.model.SmartCardStatus;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetSmartCardStatusCommand extends Command<SmartCardStatus> implements InjectableAction {

   @Inject Janet apiJanet;

   private final String smartCardId;

   public GetSmartCardStatusCommand(String smartCardId) {
      this.smartCardId = smartCardId;
   }

   public String getSmartCardId() {
      return smartCardId;
   }

   @Override
   protected void run(CommandCallback<SmartCardStatus> callback) throws Throwable {
      apiJanet.createPipe(SmartCardStatusHttpAction.class)
            .createObservableResult(new SmartCardStatusHttpAction(smartCardId))
            .map(action -> action.status().status())
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
