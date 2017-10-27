package com.worldventures.wallet.service.command.wizard;

import com.worldventures.dreamtrips.api.smart_card.user_info.model.CardUserPhone;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableUpdateCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.domain.entity.ImmutableSmartCardUser;
import com.worldventures.wallet.domain.entity.SmartCard;
import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.domain.entity.SmartCardUserPhone;
import com.worldventures.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.wallet.domain.storage.WalletStorage;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.command.SmartCardUserCommand;
import com.worldventures.wallet.service.command.http.AssociateCardUserCommand;
import com.worldventures.wallet.service.command.uploadery.SmartCardUploaderyCommand;
import com.worldventures.wallet.util.WalletFeatureHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class WizardCompleteCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet walletJanet;
   @Inject SmartCardInteractor interactor;
   @Inject WalletStorage walletStorage;
   @Inject MapperyContext mapperyContext;
   @Inject WalletFeatureHelper featureHelper;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      SmartCard smartCard = walletStorage.getSmartCard();

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
      if (photo == null) {
         return Observable.just(user);
      }
      return uploadPhotoOnServer(smartCardId, photo)
            .map(photoUrl -> attachPhotoUrlToUser(user, photoUrl));
   }

   private Observable<String> uploadPhotoOnServer(String smartCardId, SmartCardUserPhoto photo) {
      return walletJanet.createPipe(SmartCardUploaderyCommand.class, Schedulers.io())
            .createObservableResult(new SmartCardUploaderyCommand(smartCardId, photo.getUri()))
            .map(c -> c.getResult().response().uploaderyPhoto().location());
   }

   private SmartCardUser attachPhotoUrlToUser(SmartCardUser smartCardUser, String photoUrl) {
      return ImmutableSmartCardUser.builder()
            .from(smartCardUser)
            .userPhoto(new SmartCardUserPhoto(photoUrl))
            .build();
   }

   private UpdateCardUserData createRequestData(SmartCardUser smartCardUser) {
      final SmartCardUserPhoto photo = smartCardUser.userPhoto();
      final SmartCardUserPhone smartCardUserPhone = smartCardUser.phoneNumber();
      final ImmutableUpdateCardUserData.Builder userBuilder = ImmutableUpdateCardUserData.builder()
            .firstName(smartCardUser.firstName())
            .lastName(smartCardUser.lastName())
            .middleName(smartCardUser.middleName())
            .photoUrl(photo != null ? photo.getUri() : "");
      if (smartCardUserPhone != null) {
         userBuilder.phone(mapperyContext.convert(smartCardUserPhone, CardUserPhone.class));
      }
      return userBuilder.build();
   }
}
