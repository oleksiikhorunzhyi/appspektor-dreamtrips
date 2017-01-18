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
import com.worldventures.dreamtrips.wallet.service.SystemPropertiesProvider;
import com.worldventures.dreamtrips.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.storage.WizardMemoryStorage;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class WizardCompleteCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet walletJanet;
   @Inject WizardMemoryStorage wizardMemoryStorage;
   @Inject SnappyRepository snappyRepository;
   @Inject SystemPropertiesProvider propertiesProvider;

   private final SmartCard smartCard;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      uploadUserDataAndAssociateSmartCard(smartCard)
            .flatMap(sc -> walletJanet.createPipe(ActivateSmartCardCommand.class, Schedulers.io())
                  .createObservableResult(
                        new ActivateSmartCardCommand(ImmutableSmartCard.copyOf(sc)
                              .withDeviceId(propertiesProvider.deviceId()))))
            .subscribe(aVoid -> callback.onSuccess(null), callback::onFail);
   }

   private Observable<SmartCard> uploadUserDataAndAssociateSmartCard(SmartCard smartCard) {
      return uploadPhoto(smartCard, wizardMemoryStorage.getUserPhoto())
            .map(photoUrl -> updatePhoto(smartCard, photoUrl))
            .flatMap(sc ->
                  walletJanet.createPipe(AssociateCardUserCommand.class, Schedulers.io())
                        .createObservableResult(new AssociateCardUserCommand(wizardMemoryStorage.getBarcode(), createUserData(sc
                              .user()
                              .userPhoto()
                              .photoUrl())))
                        .map(associateCardUserCommand -> sc)
            );
   }

   private Observable<String> uploadPhoto(SmartCard smartCard, File file) {
      return walletJanet.createPipe(SmartCardUploaderyCommand.class, Schedulers.io())
            .createObservableResult(new SmartCardUploaderyCommand(smartCard.smartCardId(), file))
            .map(c -> c.getResult().response().uploaderyPhoto().location());
   }

   private SmartCard updatePhoto(SmartCard smartCard, String photoUrl) {
      return ImmutableSmartCard.builder().from(smartCard)
            .user(ImmutableSmartCardUser.builder().from(smartCard.user())
                  .userPhoto(ImmutableSmartCardUserPhoto.builder()
                        .from(smartCard.user().userPhoto())
                        .photoUrl(photoUrl)
                        .build())
                  .build())
            .build();
   }

   private UpdateCardUserData createUserData(String avatarUrl) {
      return ImmutableUpdateCardUserData.builder()
            .firstName(wizardMemoryStorage.getFirstName())
            .lastName(wizardMemoryStorage.getLastName())
            .middleName(wizardMemoryStorage.getMiddleName())
            .photoUrl(avatarUrl)
            .build();
   }

   public WizardCompleteCommand(SmartCard smartCard) {this.smartCard = smartCard;}
}
