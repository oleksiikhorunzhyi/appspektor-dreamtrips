package com.worldventures.dreamtrips.wallet.service.command.wizard;

import com.worldventures.dreamtrips.api.smart_card.user_info.model.CardUserPhone;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableUpdateCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData;
import com.worldventures.dreamtrips.core.api.uploadery.SmartCardUploaderyCommand;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhone;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.util.WalletFeatureHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class WizardCompleteCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet walletJanet;
   @Inject SmartCardInteractor interactor;
   @Inject SnappyRepository snappyRepository;
   @Inject MapperyContext mapperyContext;
   @Inject WalletFeatureHelper featureHelper;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      SmartCard smartCard = snappyRepository.getSmartCard();

      uploadUserPhoto(smartCard.smartCardId())
            .flatMap(user ->
                  walletJanet.createPipe(AssociateCardUserCommand.class)
                        .createObservableResult(new AssociateCardUserCommand(smartCard.smartCardId(), createRequestData(user)))
                        .flatMap(command -> featureHelper.onUserAssigned(user)))
            .subscribe(aVoid -> callback.onSuccess(null), callback::onFail);
   }

   private Observable<SmartCardUser> uploadUserPhoto(String smartCardId) {
      return interactor.smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.fetch())
            .map(Command::getResult)
            .flatMap(user -> checkUserPhotoAndUploadToServer(smartCardId, user))
            .flatMap(user -> interactor.smartCardUserPipe().createObservableResult(SmartCardUserCommand.save(user))
                  .observeOn(Schedulers.trampoline())
                  .map(command -> user));
   }

   private Observable<SmartCardUser> checkUserPhotoAndUploadToServer(String smartCardId, SmartCardUser user) {
      final SmartCardUserPhoto photo = user.userPhoto();
      if (photo == null) return Observable.just(user);
      return uploadPhotoOnServer(smartCardId, photo)
            .map(photoUrl -> attachPhotoUrlToUser(user, photoUrl));
   }

   private Observable<String> uploadPhotoOnServer(String smartCardId, SmartCardUserPhoto photo) {
      return walletJanet.createPipe(SmartCardUploaderyCommand.class, Schedulers.io())
            .createObservableResult(new SmartCardUploaderyCommand(smartCardId, photo.uri()))
            .map(c -> c.getResult().response().uploaderyPhoto().location());
   }

   private SmartCardUser attachPhotoUrlToUser(SmartCardUser smartCardUser, String photoUrl) {
      return ImmutableSmartCardUser.builder()
            .from(smartCardUser)
            .userPhoto(SmartCardUserPhoto.of(photoUrl))
            .build();
   }

   private UpdateCardUserData createRequestData(SmartCardUser smartCardUser) {
      final SmartCardUserPhoto photo = smartCardUser.userPhoto();
      final SmartCardUserPhone smartCardUserPhone = smartCardUser.phoneNumber();
      final ImmutableUpdateCardUserData.Builder userBuilder = ImmutableUpdateCardUserData.builder()
            .firstName(smartCardUser.firstName())
            .lastName(smartCardUser.lastName())
            .middleName(smartCardUser.middleName())
            .photoUrl(photo != null ? photo.uri() : "");
      if (smartCardUserPhone != null) {
         userBuilder.phone(mapperyContext.convert(smartCardUserPhone, CardUserPhone.class));
      }
      return userBuilder.build();
   }
}
