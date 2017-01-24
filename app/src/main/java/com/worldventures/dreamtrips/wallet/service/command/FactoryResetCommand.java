package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.reset.ConfirmResetCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class FactoryResetCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject @Named(JANET_WALLET) Janet walletJanet;

   private ActionPipe<ResetSmartCardCommand> resetSmartCardPipe;
   private ActionPipe<ConfirmResetCommand> confirmResetPipe;

   private boolean withEnterPin = true;

   public FactoryResetCommand(boolean withEnterPin) {
      this.withEnterPin = withEnterPin;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      createPipes();

      if (withEnterPin) {
         confirmResetPipe.createObservableResult(new ConfirmResetCommand())
               .flatMap(confirmResetCommand -> observeUnlockCard())
               .flatMap(activeSmartCardCommand -> resetSmartCard())
               .subscribe(action -> callback.onSuccess(null), callback::onFail);
      } else {
         resetSmartCard().subscribe(action -> callback.onSuccess(null), callback::onFail);
      }
   }

   private Observable<ResetSmartCardCommand> resetSmartCard() {
      return walletJanet.createPipe(ActiveSmartCardCommand.class)
            .createObservableResult(new ActiveSmartCardCommand())
            .flatMap(activeSmartCardCommand -> resetSmartCardPipe.createObservableResult(
                  new ResetSmartCardCommand(activeSmartCardCommand.getResult())));
   }

   private void createPipes() {
      resetSmartCardPipe = walletJanet.createPipe(ResetSmartCardCommand.class, Schedulers.io());
      confirmResetPipe = walletJanet.createPipe(ConfirmResetCommand.class, Schedulers.io());
   }

   private Observable<ActiveSmartCardCommand> observeUnlockCard() {
      return smartCardInteractor.activeSmartCardPipe()
            .observeSuccess()
            .filter(activeSmartCardCommand -> !activeSmartCardCommand.getResult().lock())
            .take(1)
            .delay(5000, TimeUnit.MILLISECONDS);
   }
}
