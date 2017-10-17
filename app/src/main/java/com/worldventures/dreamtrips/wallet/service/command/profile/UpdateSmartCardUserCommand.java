package com.worldventures.dreamtrips.wallet.service.command.profile;

import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.CardUserPhone;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableUpdateCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhone;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.WalletSocialInfoProvider;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.ValidateDisplayTypeDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.uploadery.SmartCardUploaderyCommand;
import com.worldventures.dreamtrips.wallet.util.CachedPhotoUtil;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.NetworkUnavailableException;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.user.RemoveUserPhotoAction;
import io.techery.janet.smartcard.action.user.UpdateUserAction;
import io.techery.janet.smartcard.model.ImmutableUser;
import io.techery.mappery.MapperyContext;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class UpdateSmartCardUserCommand extends Command<SmartCardUser> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject WalletNetworkService networkService;
   @Inject WalletSocialInfoProvider socialInfoProvider;
   @Inject UpdateProfileManager updateProfileManager;
   @Inject MapperyContext mapperyContext;
   @Inject CachedPhotoUtil cachedPhotoUtil;

   private final ChangedFields changedFields;
   private final boolean forceUpdateDisplayType;

   public UpdateSmartCardUserCommand(ChangedFields changedFields, boolean forceUpdateDisplayType) {
      this.changedFields = changedFields;
      this.forceUpdateDisplayType = forceUpdateDisplayType;
   }

   @Override
   protected void run(CommandCallback<SmartCardUser> callback) throws Throwable {
      validateData();
      validateNetwork();

      smartCardInteractor.validateDisplayTypeDataPipe()
            .createObservableResult(new ValidateDisplayTypeDataCommand(
                  changedFields.photo() != null, changedFields.phone() != null, forceUpdateDisplayType))
            .doOnNext(command -> updateProfileManager.attachChangedFields(changedFields))
            .flatMap(command -> Observable.zip(
                  smartCardInteractor.activeSmartCardPipe()
                        .createObservableResult(new ActiveSmartCardCommand()),
                  smartCardInteractor.smartCardUserPipe()
                        .createObservableResult(SmartCardUserCommand.fetch()),
                  Pair::new))
            .flatMap(pair -> uploadData(pair.first.getResult().smartCardId(), pair.second.getResult()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private void validateData() throws FormatException {
      WalletValidateHelper.validateUserFullNameOrThrow(
            changedFields.firstName(),
            changedFields.middleName(),
            changedFields.lastName());
   }

   private void validateNetwork() {
      if (!networkService.isAvailable()) {
         throw new NetworkUnavailableException();
      }
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

      dataBuilder.photoUrl(changedFields.photo() != null ? changedFields.photo().uri() : null);
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
                     .memberId(socialInfoProvider.userId())
                     .barcodeId(Long.parseLong(scId))
                     .memberStatus(socialInfoProvider.memberStatus())
                     .build()))
               .map(action -> userData);
      } else {
         return Observable.just(userData);
      }
   }

   private boolean needToUpdate(SmartCardUser user) {
      return !changedFields.firstName().equals(user.firstName())
            || !changedFields.middleName().equals(user.middleName())
            || !changedFields.lastName().equals(user.lastName())
            || !equalsDirectly(user.phoneNumber(), changedFields.phone());
   }

   private boolean equalsDirectly(Object a, Object b) {
      return (a == b) || (a != null && a.equals(b));
   }

   private Observable<UpdateCardUserData> uploadPhotoIfNeed(SmartCardUser user, String smartCardId,
         UpdateCardUserData updateUserData) {
      final SmartCardUserPhoto newPhoto = changedFields.photo();
      if (newPhoto != null) {

         clearUserImageCache(user.userPhoto());

         return janet.createPipe(UpdateSmartCardUserPhotoCommand.class)
               .createObservableResult(new UpdateSmartCardUserPhotoCommand(newPhoto.uri()))
               .flatMap(aVoid -> janet.createPipe(SmartCardUploaderyCommand.class, Schedulers.io())
                     .createObservableResult(new SmartCardUploaderyCommand(smartCardId, newPhoto.uri())))
               .map(command -> ImmutableUpdateCardUserData.builder()
                     .from(updateUserData)
                     //uri saved in UpdateProfileManager
                     .photoUrl(command.getResult().response().uploaderyPhoto().location())
                     .build());
      } else {
         return smartCardInteractor.removeUserPhotoActionPipe()
               .createObservableResult(new RemoveUserPhotoAction())
               .map(action -> updateUserData);
      }
   }

   private void clearUserImageCache(@Nullable SmartCardUserPhoto photo) {
      if (photo != null) {
         cachedPhotoUtil.removeCachedPhoto(photo.uri());
      }
   }
}
