package com.worldventures.dreamtrips.wallet.service.command.profile;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableUpdateCardUserData;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData;
import com.worldventures.dreamtrips.core.api.uploadery.SmartCardUploaderyCommand;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.util.SmartCardAvatarHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.util.NetworkUnavailableException;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.user.UpdateUserAction;
import io.techery.janet.smartcard.action.user.UpdateUserPhotoAction;
import io.techery.janet.smartcard.model.ImmutableUser;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;
import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class UpdateSmartCardUserCommand extends Command<SmartCard> implements InjectableAction {

   @Inject @Named(JANET_API_LIB) Janet janetApi;
   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SmartCardAvatarHelper smartCardAvatarHelper;
   @Inject WalletNetworkService networkService;
   @Inject SnappyRepository snappyRepository;
   @Inject UpdateDataHolder updateDataHolder;
   @Inject SessionHolder<UserSession> userSessionHolder;
   @Inject UpdateProfileManager updateProfileManager;

   private final ChangedFields changedFields;

   public UpdateSmartCardUserCommand(ChangedFields changedFields) {
      this.changedFields = changedFields;
   }

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      validateData();
      if (!networkService.isAvailable()) throw new NetworkUnavailableException();
      updateDataHolder.saveChanging(changedFields);

      janet.createPipe(ActiveSmartCardCommand.class)
            .createObservableResult(new ActiveSmartCardCommand())
            .map(Command::getResult)
            .flatMap(this::uploadData)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private void validateData() {
      // TODO: 12/15/16 middle is mandatory ?
      WalletValidateHelper.validateUserFullName(
            changedFields.firstName(),
            changedFields.middleName(),
            changedFields.lastName());
   }

   private Observable<SmartCard> uploadData(SmartCard smartCard) {
      return createUpdateCardUserData(smartCard)
            .flatMap(updateCardUserData -> updateProfileManager.uploadData(smartCard, updateCardUserData));

   }

   private Observable<UpdateCardUserData> createUpdateCardUserData(SmartCard smartCard) {
      return updateNameOnSmartCard(smartCard)
            .flatMap(userData -> uploadPhoto(smartCard, userData));
   }

   private Observable<UpdateCardUserData> updateNameOnSmartCard(SmartCard smartCard) {
      final SmartCardUser user = smartCard.user();
      boolean needUpdate = false;
      final ImmutableUpdateCardUserData.Builder dataBuilder = ImmutableUpdateCardUserData.builder()
            .photoUrl(user.userPhoto().photoUrl()); // photoUrl is mandatory field for API

      dataBuilder.firstName(changedFields.firstName());
      dataBuilder.middleName(changedFields.middleName());
      dataBuilder.lastName(changedFields.lastName());

      if (!changedFields.firstName().equals(user.firstName())
            || !changedFields.middleName().equals(user.middleName())
            || !changedFields.lastName().equals(user.lastName())
            || !changedFields.photo().photoUrl().equals(user.userPhoto().photoUrl())) {
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
                     .barcodeId(Long.parseLong(smartCard.smartCardId()))
                     .memberStatus(UserSmartCardUtils.obtainMemberStatus(userSessionHolder))
                     .build()))
               .map(action -> userData);
      } else {
         return Observable.just(userData);
      }
   }

   private Observable<UpdateCardUserData> uploadPhoto(SmartCard smartCard, UpdateCardUserData userData) {
      final SmartCardUserPhoto photo = changedFields.photo();
      if (photo != null) {
         return janet.createPipe(UpdateUserPhotoAction.class)
               .createObservableResult(new UpdateUserPhotoAction(smartCardAvatarHelper.convertBytesForUpload(photo.monochrome())))
               .flatMap(action -> janet.createPipe(SmartCardUploaderyCommand.class)
                     .createObservableResult(new SmartCardUploaderyCommand(smartCard.smartCardId(), photo.original())))
               .map(command -> ImmutableUpdateCardUserData.builder()
                     .from(userData)
                     //photoUrl saved in UpdateProfileManager
                     .photoUrl(command.getResult().response().uploaderyPhoto().location())
                     .build());
      } else {
         return Observable.just(userData);
      }
   }
}
