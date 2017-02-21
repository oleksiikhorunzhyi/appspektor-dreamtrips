package com.worldventures.dreamtrips.wallet.service.command.wizard;

import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableUpdateCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData;
import com.worldventures.dreamtrips.core.api.uploadery.SmartCardUploaderyCommand;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class WizardCompleteCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet walletJanet;
   @Inject SmartCardInteractor interactor;
   @Inject SnappyRepository snappyRepository;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      SmartCard smartCard = snappyRepository.getSmartCard();

      uploadUserPhoto(smartCard.smartCardId())
            .flatMap(user ->
                  walletJanet.createPipe(AssociateCardUserCommand.class)
                        .createObservableResult(new AssociateCardUserCommand(smartCard.smartCardId(), createRequestData(user)))
                        .map(associateCardUserCommand -> user))
            .subscribe(aVoid -> callback.onSuccess(null), callback::onFail);
   }

   private Observable<SmartCardUser> uploadUserPhoto(String smartCardId) {
      return interactor.smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.fetch())
            .map(Command::getResult)
            .flatMap(user -> uploadPhotoOnServer(smartCardId, user.userPhoto().original())
                  .map(photoUrl -> attachPhotoUrlToUser(user, photoUrl)))
            .flatMap(sc ->
                  walletJanet.createPipe(AssociateCardUserCommand.class, Schedulers.io())
                        .createObservableResult(new AssociateCardUserCommand(wizardMemoryStorage.getBarcode(), createUserData(sc
                              .user()
                              .userPhoto()
                              .photoUrl())))
                        .map(command -> ImmutableSmartCard.builder() //// TODO: 2/15/17 SendFeedbackCommand use this fields
                              .from(sc)
                              .deviceAddress(command.getResult().bleAddress())
                              .serialNumber(command.getResult().serialNumber())
                              .build()
                        )
            );
   }

   private Observable<String> uploadPhotoOnServer(String smartCardId, File file) {
      return walletJanet.createPipe(SmartCardUploaderyCommand.class)
            .createObservableResult(new SmartCardUploaderyCommand(smartCardId, file))
            .map(c -> c.getResult().response().uploaderyPhoto().location());
   }

   private SmartCardUser attachPhotoUrlToUser(SmartCardUser smartCardUser, String photoUrl) {
      return ImmutableSmartCardUser.builder()
            .from(smartCardUser)
            .userPhoto(ImmutableSmartCardUserPhoto.builder()
                  .from(smartCardUser.userPhoto())
                  .photoUrl(photoUrl)
                  .build())
            .build();
   }

   private UpdateCardUserData createRequestData(SmartCardUser smartCardUser) {
      return ImmutableUpdateCardUserData.builder()
            .firstName(smartCardUser.firstName())
            .lastName(smartCardUser.lastName())
            .middleName(smartCardUser.middleName())
            .photoUrl(smartCardUser.userPhoto().photoUrl())
            .build();
   }
}
