package com.worldventures.dreamtrips.wallet.service.command;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;
import com.worldventures.dreamtrips.util.SmartCardAvatarHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.profile.ChangedFields;
import com.worldventures.dreamtrips.wallet.service.command.profile.UserSmartCardUtils;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.MissedAvatarException;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.user.AssignUserAction;
import io.techery.janet.smartcard.action.user.UpdateUserPhotoAction;
import io.techery.janet.smartcard.model.ImmutableUser;
import io.techery.janet.smartcard.model.User;
import io.techery.mappery.MapperyContext;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class SetupUserDataCommand extends Command<SmartCardUser> implements InjectableAction {

   @Inject Janet janetGeneric;
   @Inject @Named(JANET_WALLET) Janet janetWallet;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject SessionHolder<UserSession> userSessionHolder;
   @Inject SmartCardAvatarHelper smartCardAvatarHelper;
   @Inject MapperyContext mappery;

   private final ChangedFields changedFields;

   public SetupUserDataCommand(ChangedFields changedFields) {
      this.changedFields = changedFields;
   }

   @Override
   protected void run(CommandCallback<SmartCardUser> callback) throws Throwable {
      validateUserData()
            .flatMap(aVoid -> createUserForSmartCard())
            .flatMap(user -> uploadOnSmartCard(user)
                  .map(aVoid -> convertToSmartCardUser(user)))
            .flatMap(user -> smartCardInteractor.smartCardUserPipe()
                  .createObservableResult(SmartCardUserCommand.save(user)))
            .map(Command::getResult)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private SmartCardUser convertToSmartCardUser(User user) {
      return ImmutableSmartCardUser.builder()
            .from(mappery.convert(user, SmartCardUser.class))
            .userPhoto(changedFields.photo())
            .phoneNumber(changedFields.phone())
            .build();
   }

   private Observable<Void> uploadOnSmartCard(User user) {
      return janetWallet
            .createPipe(AssignUserAction.class).createObservableResult(new AssignUserAction(user))
            .flatMap(action -> Observable
                  .fromCallable(this::getAvatarAsByteArray)
                  .flatMap(bytesArray -> janetWallet.createPipe(UpdateUserPhotoAction.class)
                        .createObservableResult(new UpdateUserPhotoAction(bytesArray)))
            ).map(action -> null);
   }

   private Observable<Void> validateUserData() {
      try {
         if (changedFields.photo() == null || changedFields.photo().original() == null || !changedFields.photo().original().exists()) {
            throw new MissedAvatarException();
         }
         WalletValidateHelper.validateUserFullNameOrThrow(
               changedFields.firstName(),
               changedFields.middleName(),
               changedFields.lastName()
         );
      } catch (FormatException | MissedAvatarException e) {
         return Observable.error(e);
      }
      return Observable.just(null);
   }

   private Observable<User> createUserForSmartCard() {
      return smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new ActiveSmartCardCommand())
            .map(command -> ImmutableUser.builder()
                  .firstName(changedFields.firstName())
                  .lastName(changedFields.lastName())
                  .middleName(changedFields.middleName())
                  .phoneNum(fetchPhone())
                  .memberStatus(UserSmartCardUtils.obtainMemberStatus(userSessionHolder))
                  .memberId(userSessionHolder.get().get().getUser().getId())
                  .barcodeId(Long.valueOf(command.getResult().smartCardId()))
                  .build());
   }

   private String fetchPhone() {
      return changedFields.phone() != null ? changedFields.phone().fullPhoneNumber() : null;
   }

   private byte[] getAvatarAsByteArray() throws IOException {
      final int[][] ditheredImageArray =
            smartCardAvatarHelper.toMonochrome(changedFields.photo().original(), ImageUtils.DEFAULT_IMAGE_SIZE);
      return smartCardAvatarHelper.convertBytesForUpload(ditheredImageArray);
   }


}
