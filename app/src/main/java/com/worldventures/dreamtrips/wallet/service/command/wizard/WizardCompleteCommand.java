package com.worldventures.dreamtrips.wallet.service.command.wizard;

import android.net.Uri;

import com.worldventures.dreamtrips.api.smart_card.user_info.UpdateCardUserHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableUpdateCardUserData;
import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
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

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;
import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class WizardCompleteCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_API_LIB) Janet apiJanet;
   @Inject @Named(JANET_WALLET) Janet walletJanet;
   @Inject WizardMemoryStorage wizardMemoryStorage;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      walletJanet.createPipe(EnableLockUnlockDeviceAction.class)
            .createObservableResult(new EnableLockUnlockDeviceAction(true))
            .flatMap(action -> walletJanet.createPipe(AssociateCardUserCommand.class)
                  .createObservableResult(new AssociateCardUserCommand(wizardMemoryStorage.getBarcode())))
            .flatMap(action -> uploadUserData())
            .subscribe(aVoid -> callback.onSuccess(null), callback::onFail);
   }

   private Observable<? extends UpdateCardUserHttpAction> uploadUserData() {
      return walletJanet.createPipe(SimpleUploaderyCommand.class)
            .createObservableResult(new SimpleUploaderyCommand(Uri.fromFile(wizardMemoryStorage.getUserPhoto())
                  .toString()))
            .map(c -> c.getResult().getPhotoUploadResponse().getLocation())
            .flatMap(avatarUrl -> {
                     ImmutableUpdateCardUserData cardUserData = ImmutableUpdateCardUserData.builder()
                           .nameToDisplay(wizardMemoryStorage.getFullName())
                           .photoUrl(avatarUrl)
                           .build();
                     return apiJanet.createPipe(UpdateCardUserHttpAction.class)
                           .createObservableResult(new UpdateCardUserHttpAction(Long.parseLong(wizardMemoryStorage.getBarcode()), cardUserData));
                  }
            );
   }

}
