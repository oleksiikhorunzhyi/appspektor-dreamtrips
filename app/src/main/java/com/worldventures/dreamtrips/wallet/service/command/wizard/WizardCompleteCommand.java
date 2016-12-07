package com.worldventures.dreamtrips.wallet.service.command.wizard;

import android.net.Uri;

import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableUpdateCardUserData;
import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.storage.WizardMemoryStorage;
import com.worldventures.dreamtrips.wallet.util.FormatException;

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

   private String firstName, lastName, middleName = null;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      String[] nameParts = wizardMemoryStorage.getFullName().split(" ");
      if (nameParts.length < 2 || nameParts.length > 3) throw new FormatException();
      if (nameParts.length == 2) {
         firstName = nameParts[0];
         lastName = nameParts[1];
      } else {
         firstName = nameParts[0];
         middleName = nameParts[1];
         lastName = nameParts[2];
      }

      walletJanet.createPipe(EnableLockUnlockDeviceAction.class)
            .createObservableResult(new EnableLockUnlockDeviceAction(true))
            .flatMap(action -> uploadUserDataAndAssociateSmartCard())
            .subscribe(aVoid -> callback.onSuccess(null), callback::onFail);
   }

   private Observable<? extends AssociateCardUserCommand> uploadUserDataAndAssociateSmartCard() {
      return walletJanet.createPipe(SimpleUploaderyCommand.class)
            .createObservableResult(new SimpleUploaderyCommand(Uri.fromFile(wizardMemoryStorage.getUserPhoto())
                  .toString()))
            .map(c -> c.getResult().getPhotoUploadResponse().getLocation())
            .flatMap(avatarUrl -> {
                     ImmutableUpdateCardUserData cardUserData = ImmutableUpdateCardUserData.builder()
                           .nameToDisplay(wizardMemoryStorage.getFullName())
                           .displayFirstName(firstName)
                           .displayLastName(lastName)
                           .displayMiddleName(middleName)
                           .photoUrl(avatarUrl)
                           .build();
                     return walletJanet.createPipe(AssociateCardUserCommand.class)
                           .createObservableResult(new AssociateCardUserCommand(wizardMemoryStorage.getBarcode(), cardUserData));
                  }
            );
   }
}
