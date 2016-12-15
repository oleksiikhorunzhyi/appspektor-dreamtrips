package com.worldventures.dreamtrips.wallet.service.command.wizard;

import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableUpdateCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData;
import com.worldventures.dreamtrips.core.api.uploadery.SmartCardUploaderyCommand;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.storage.WizardMemoryStorage;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.EnableLockUnlockDeviceAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class WizardCompleteCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet walletJanet;
   @Inject WizardMemoryStorage wizardMemoryStorage;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      walletJanet.createPipe(EnableLockUnlockDeviceAction.class)
            .createObservableResult(new EnableLockUnlockDeviceAction(true))
            .flatMap(action -> uploadUserDataAndAssociateSmartCard())
            .subscribe(aVoid -> callback.onSuccess(null), callback::onFail);
   }

   private Observable<? extends AssociateCardUserCommand> uploadUserDataAndAssociateSmartCard() {
      // TODO: 12/16/16 save photo url
      return walletJanet.createPipe(SmartCardUploaderyCommand.class)
            .createObservableResult(
                  new SmartCardUploaderyCommand(wizardMemoryStorage.getBarcode(), wizardMemoryStorage.getUserPhoto()))
            .map(c -> c.getResult().response().uploaderyPhoto().location())
            .flatMap(avatarUrl -> walletJanet.createPipe(AssociateCardUserCommand.class)
                  .createObservableResult(new AssociateCardUserCommand(wizardMemoryStorage.getBarcode(), createUserData(avatarUrl)))
            );
   }

   private UpdateCardUserData createUserData(String avatarUrl) {
      return ImmutableUpdateCardUserData.builder()
            .firstName(wizardMemoryStorage.getFirstName())
            .lastName(wizardMemoryStorage.getLastName())
            .middleName(wizardMemoryStorage.getMiddleName())
            .photoUrl(avatarUrl)
            .build();
   }
}
