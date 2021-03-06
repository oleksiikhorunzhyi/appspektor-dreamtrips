package com.worldventures.wallet.service.command;

import android.support.annotation.Nullable;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.domain.entity.SmartCardUserPhone;
import com.worldventures.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletSocialInfoProvider;
import com.worldventures.wallet.service.command.settings.general.display.RestoreDefaultDisplayTypeCommand;
import com.worldventures.wallet.service.profile.UpdateSmartCardUserPhotoCommand;
import com.worldventures.wallet.util.FormatException;
import com.worldventures.wallet.util.WalletValidateHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.user.AssignUserAction;
import io.techery.janet.smartcard.model.ImmutableUser;
import io.techery.janet.smartcard.model.User;
import rx.Observable;

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class SetupUserDataCommand extends Command<SmartCardUser> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janetWallet;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject WalletSocialInfoProvider socialInfoProvider;

   private final SmartCardUser userData;

   public SetupUserDataCommand(SmartCardUser user) {
      this.userData = user;
   }

   @Override
   protected void run(CommandCallback<SmartCardUser> callback) throws Throwable {
      validateUserData()
            .flatMap(aVoid -> createUserForSmartCard())
            .flatMap(this::uploadOnSmartCard)
            .flatMap(aVoid -> restoreSmartCardDisplayType())
            .flatMap(aVoid -> cacheSmartCardUser())
            .map(Command::getResult)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Void> validateUserData() {
      try {
         WalletValidateHelper.validateUserFullNameOrThrow(
               userData.getFirstName(),
               userData.getMiddleName(),
               userData.getLastName()
         );
      } catch (FormatException e) {
         return Observable.error(e);
      }
      return Observable.just(null);
   }

   private Observable<User> createUserForSmartCard() {
      return smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new ActiveSmartCardCommand())
            .map(command -> ImmutableUser.builder()
                  .firstName(userData.getFirstName())
                  .lastName(userData.getLastName())
                  .middleName(userData.getMiddleName())
                  .phoneNum(fetchPhone(userData))
                  .memberStatus(socialInfoProvider.memberStatus())
                  .memberId(socialInfoProvider.userId())
                  .barcodeId(Long.valueOf(command.getResult().getSmartCardId()))
                  .build());
   }

   @Nullable
   private String fetchPhone(SmartCardUser userData) {
      final SmartCardUserPhone phone = userData.getPhoneNumber();
      return phone != null ? phone.fullPhoneNumber() : null;
   }

   private Observable<Void> uploadOnSmartCard(User user) {
      return janetWallet.createPipe(AssignUserAction.class)
            .createObservableResult(new AssignUserAction(user))
            .flatMap(action -> uploadUserPhoto()).map(action -> null);
   }

   private Observable<Void> uploadUserPhoto() {
      final SmartCardUserPhoto photo = userData.getUserPhoto();
      if (photo == null) {
         return Observable.just(null);
      }
      return janetWallet.createPipe(UpdateSmartCardUserPhotoCommand.class)
            .createObservableResult(new UpdateSmartCardUserPhotoCommand(photo.getUri()))
            .map(a -> null);
   }

   private Observable<RestoreDefaultDisplayTypeCommand> restoreSmartCardDisplayType() {
      return smartCardInteractor.restoreDefaultDisplayTypePipe()
            .createObservableResult(new RestoreDefaultDisplayTypeCommand(userData));
   }

   private Observable<SmartCardUserCommand> cacheSmartCardUser() {
      return smartCardInteractor.smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.save(userData));
   }
}
