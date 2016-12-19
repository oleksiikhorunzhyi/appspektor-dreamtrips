package com.worldventures.dreamtrips.wallet.service.command.wizard;

import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableUpdateCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData;
import com.worldventures.dreamtrips.core.api.uploadery.SmartCardUploaderyCommand;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.storage.WizardMemoryStorage;

import java.io.File;

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
   @Inject SnappyRepository snappyRepository;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      walletJanet.createPipe(EnableLockUnlockDeviceAction.class)
            .createObservableResult(new EnableLockUnlockDeviceAction(true))
            .flatMap(action -> uploadUserDataAndAssociateSmartCard())
            .subscribe(aVoid -> callback.onSuccess(null), callback::onFail);
   }

   private Observable<? extends AssociateCardUserCommand> uploadUserDataAndAssociateSmartCard() {
      return walletJanet.createPipe(GetActiveSmartCardCommand.class)
            .createObservableResult(new GetActiveSmartCardCommand())
            .map(Command::getResult)
            .flatMap(smartCard ->
                  uploadPhoto(smartCard, wizardMemoryStorage.getUserPhoto())
                        .doOnNext(photoUrl -> updatePhoto(smartCard, photoUrl))
            )
            .flatMap(avatarUrl ->
                  walletJanet.createPipe(AssociateCardUserCommand.class)
                        .createObservableResult(new AssociateCardUserCommand(wizardMemoryStorage.getBarcode(), createUserData(avatarUrl)))
            );
   }

   private Observable<String> uploadPhoto(SmartCard smartCard, File file) {
      return walletJanet.createPipe(SmartCardUploaderyCommand.class)
            .createObservableResult(new SmartCardUploaderyCommand(smartCard.smartCardId(), file))
            .map(c -> c.getResult().response().uploaderyPhoto().location());
   }

   private SmartCard updatePhoto(SmartCard smartCard, String photoUrl) {
      SmartCard newSmartCard = ImmutableSmartCard.builder().from(smartCard)
            .user(ImmutableSmartCardUser.builder().from(smartCard.user())
                  .userPhoto(ImmutableSmartCardUserPhoto.builder()
                        .from(smartCard.user().userPhoto())
                        .photoUrl(photoUrl)
                        .build())
                  .build())
            .build();
      snappyRepository.saveSmartCard(newSmartCard);
      return newSmartCard;
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
