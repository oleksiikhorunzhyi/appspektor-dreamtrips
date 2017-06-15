package com.worldventures.dreamtrips.wallet.service.command.profile;

import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.CardUserPhone;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableUpdateCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData;
import com.worldventures.dreamtrips.core.api.uploadery.SmartCardUploaderyCommand;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhone;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.util.CachedPhotoUtil;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.NetworkUnavailableException;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.user.UpdateUserAction;
import io.techery.janet.smartcard.model.ImmutableUser;
import io.techery.mappery.MapperyContext;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class UpdateSmartCardUserCommand extends Command<SmartCardUser> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject WalletNetworkService networkService;
   @Inject SessionHolder<UserSession> userSessionHolder;
   @Inject UpdateProfileManager updateProfileManager;
   @Inject MapperyContext mapperyContext;
   @Inject CachedPhotoUtil cachedPhotoUtil;

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
      final ImmutableUpdateCardUserData.Builder dataBuilder = ImmutableUpdateCardUserData.builder();
      final SmartCardUserPhoto userPhoto = user.userPhoto();

      dataBuilder.photoUrl(userPhoto != null ? userPhoto.uri() : "");
      dataBuilder.firstName(changedFields.firstName());
      dataBuilder.middleName(changedFields.middleName());
      dataBuilder.lastName(changedFields.lastName());

      if (changedFields.phone() != null) {
         dataBuilder.phone(mapperyContext.convert(changedFields.phone(), CardUserPhone.class));
      }

      final UpdateCardUserData userData = dataBuilder.build();
      final SmartCardUserPhone phone = changedFields.phone();
      if (needToUpdate(user)) {
         return janet.createPipe(UpdateUserAction.class)
               .createObservableResult(new UpdateUserAction(ImmutableUser.builder()
                     .firstName(changedFields.firstName())
                     .middleName(changedFields.middleName())
                     .lastName(changedFields.lastName())
                     .phoneNum(phone != null ? phone.fullPhoneNumber() : null)
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

   private boolean needToUpdate(SmartCardUser user) {
      final SmartCardUserPhone phoneNumber = user.phoneNumber();
      return !changedFields.firstName().equals(user.firstName())
            || !changedFields.middleName().equals(user.middleName())
            || !changedFields.lastName().equals(user.lastName())
            || ( phoneNumber != null && !phoneNumber.equals(changedFields.phone()));
   }

   private Observable<UpdateCardUserData> uploadPhotoIfNeed(SmartCardUser user, String smartCardId,
         UpdateCardUserData userData) {
      final SmartCardUserPhoto newPhoto = changedFields.photo();
      if (newPhoto != null) {

         clearUserImageCache(user.userPhoto());

         return janet.createPipe(UpdateSmartCardUserPhotoCommand.class)
               .createObservableResult(new UpdateSmartCardUserPhotoCommand(newPhoto.uri()))
               .flatMap(aVoid -> janet.createPipe(SmartCardUploaderyCommand.class, Schedulers.io())
                     .createObservableResult(new SmartCardUploaderyCommand(smartCardId, newPhoto.uri())))
               .map(command -> ImmutableUpdateCardUserData.builder()
                     .from(userData)
                     //uri saved in UpdateProfileManager
                     .photoUrl(command.getResult().response().uploaderyPhoto().location())
                     .build());
      } else {
         final SmartCardUserPhoto userPhoto = user.userPhoto();
         return Observable.just(ImmutableUpdateCardUserData.copyOf(userData)
               .withPhotoUrl(userPhoto != null ? userPhoto.uri() : null));
      }
   }

   private void clearUserImageCache(@Nullable SmartCardUserPhoto photo) {
      if (photo != null) {
         cachedPhotoUtil.removeCachedPhoto(photo.uri());
      }
   }
}
