package com.worldventures.dreamtrips.wallet.service.command;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhone;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletSocialInfoProvider;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserPhotoCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.RestoreDefaultDisplayTypeCommand;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.user.AssignUserAction;
import io.techery.janet.smartcard.model.ImmutableUser;
import io.techery.janet.smartcard.model.User;
import rx.Observable;

import static com.worldventures.dreamtrips.wallet.di.WalletJanetModule.JANET_WALLET;

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
               userData.firstName(),
               userData.middleName(),
               userData.lastName()
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
                  .firstName(userData.firstName())
                  .lastName(userData.lastName())
                  .middleName(userData.middleName())
                  .phoneNum(fetchPhone(userData))
                  .memberStatus(socialInfoProvider.memberStatus())
                  .memberId(socialInfoProvider.userId())
                  .barcodeId(Long.valueOf(command.getResult().smartCardId()))
                  .build());
   }

   @Nullable
   private String fetchPhone(SmartCardUser userData) {
      final SmartCardUserPhone phone = userData.phoneNumber();
      return phone != null ? phone.fullPhoneNumber() : null;
   }

   private Observable<Void> uploadOnSmartCard(User user) {
      return janetWallet.createPipe(AssignUserAction.class)
            .createObservableResult(new AssignUserAction(user))
            .flatMap(action -> uploadUserPhoto()).map(action -> null);
   }

   private Observable<Void> uploadUserPhoto() {
      final SmartCardUserPhoto photo = userData.userPhoto();
      if (photo == null) return Observable.just(null);
      return janetWallet.createPipe(UpdateSmartCardUserPhotoCommand.class)
            .createObservableResult(new UpdateSmartCardUserPhotoCommand(photo.uri()))
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