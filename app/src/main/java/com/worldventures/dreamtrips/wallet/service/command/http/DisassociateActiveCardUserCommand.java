package com.worldventures.dreamtrips.wallet.service.command.http;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class DisassociateActiveCardUserCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject WizardInteractor wizardInteractor;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      smartCardInteractor.activeSmartCardPipe().createObservableResult(new GetActiveSmartCardCommand()).flatMap(action -> {
         return wizardInteractor.disassociatePipe()
               .createObservableResult(new DisassociateCardUserCommand(action.getResult().smartCardId()));
      }).subscribe(o -> callback.onSuccess(null), t -> callback.onFail(t));
   }
}
