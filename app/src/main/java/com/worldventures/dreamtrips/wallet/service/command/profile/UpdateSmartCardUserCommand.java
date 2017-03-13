package com.worldventures.dreamtrips.wallet.service.command.profile;

import android.support.v4.util.Pair;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableUpdateCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData;
import com.worldventures.dreamtrips.core.api.uploadery.SmartCardUploaderyCommand;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;
import com.worldventures.dreamtrips.util.SmartCardAvatarHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.NetworkUnavailableException;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.user.UpdateUserAction;
import io.techery.janet.smartcard.action.user.UpdateUserPhotoAction;
import io.techery.janet.smartcard.model.ImmutableUser;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class UpdateSmartCardUserCommand extends Command<SmartCardUser> implements InjectableAction {

   @Inject Janet janetApi;
   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SmartCardAvatarHelper smartCardAvatarHelper;
   @Inject WalletNetworkService networkService;
   @Inject SessionHolder<UserSession> userSessionHolder;
   @Inject UpdateProfileManager updateProfileManager;

   private final ChangedFields changedFields;

   public UpdateSmartCardUserCommand(ChangedFields changedFields) {
      this.changedFields = changedFields;
   }

   @Override
   protected void run(CommandCallback<SmartCardUser> callback) throws Throwable {
      validateData();
      if (!networkService.isAvailable()) throw new NetworkUnavailableException();
      updateProfileManager.attachChangedFields(changedFields);

      Observable.zip(
            janet.createPipe(ActiveSmartCardCommand.class)
                  .createObservableResult(new ActiveSmartCardCommand()),
            janet.createPipe(SmartCardUserCommand.class)
                  .createObservableResult(SmartCardUserCommand.fetch()),
            Pair::new)
            .flatMap(pair -> uploadData(pair.first.getResult().smartCardId(), pair.second.getResult()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private void validateData() throws FormatException {
      WalletValidateHelper.validateUserFullNameOrThrow(
            changedFields.firstName(),
            changedFields.middleName(),
            changedFields.lastName());
   }

   private Observable<SmartCardUser> uploadData(String smartCardId, SmartCardUser user) {
      return pushToSmartCard(smartCardId, user)
            .flatMap(updateCardUserData -> updateProfileManager.uploadData(smartCardId, updateCardUserData));
   }

   private Observable<UpdateCardUserData> pushToSmartCard(String smartCardId, SmartCardUser user) {
      return updateNameOnSmartCard(smartCardId, user)
            .flatMap(userData -> uploadPhotoIfNeed(user, smartCardId, userData));
   }

   private Observable<UpdateCardUserData> updateNameOnSmartCard(String scId, SmartCardUser user) {
      boolean needUpdate = false;
      final ImmutableUpdateCardUserData.Builder dataBuilder = ImmutableUpdateCardUserData.builder()
            .photoUrl(user.userPhoto().photoUrl().toString()); // photoUrl is mandatory field for API

      dataBuilder.firstName(changedFields.firstName());
      dataBuilder.middleName(changedFields.middleName());
      dataBuilder.lastName(changedFields.lastName());

      if (!changedFields.firstName().equals(user.firstName())
            || !changedFields.middleName().equals(user.middleName())
            || !changedFields.lastName().equals(user.lastName())) {
         needUpdate = true;
      }

      final UpdateCardUserData userData = dataBuilder.build();

      if (needUpdate) {
         return janet.createPipe(UpdateUserAction.class)
               .createObservableResult(new UpdateUserAction(ImmutableUser.builder()
                     .firstName(changedFields.firstName())
                     .middleName(changedFields.middleName())
                     .lastName(changedFields.lastName())
                     .isUserAssigned(true)
                     .memberId(userSessionHolder.get().get().getUser().getId())
                     .barcodeId(Long.parseLong(scId))
                     .memberStatus(UserSmartCardUtils.obtainMemberStatus(userSessionHolder))
                     .build()))
               .map(action -> userData);
      } else {
         return Observable.just(userData);
      }
   }

   private Observable<UpdateCardUserData> uploadPhotoIfNeed(SmartCardUser user, String smartCardId, UpdateCardUserData userData) {
      final SmartCardUserPhoto photo = changedFields.photo();
      if (photo != null) {

         return Observable
               .fromCallable(() -> getAvatarAsByteArray(photo))
               .flatMap(bytes ->  janet.createPipe(UpdateUserPhotoAction.class)
                     .createObservableResult(new UpdateUserPhotoAction(bytes)))
               .flatMap(action -> janet.createPipe(SmartCardUploaderyCommand.class)
                     .createObservableResult(new SmartCardUploaderyCommand(smartCardId, photo.original())))
               .map(command -> ImmutableUpdateCardUserData.builder()
                     .from(userData)
                     //photoUrl saved in UpdateProfileManager
                     .photoUrl(command.getResult().response().uploaderyPhoto().location())
                     .build());
      } else {
         return Observable.just(ImmutableUpdateCardUserData.copyOf(userData).withPhotoUrl(user.userPhoto().photoUrl().toString()));
      }
   }

   private byte[] getAvatarAsByteArray(SmartCardUserPhoto avatar) throws IOException {
      final File ditheredImageFile =
            smartCardAvatarHelper.toMonochromeFile(avatar.original(), ImageUtils.DEFAULT_IMAGE_SIZE);
      return smartCardAvatarHelper.convertBytesForUpload(ditheredImageFile);
   }
}
