package com.worldventures.dreamtrips.wallet.service.command.profile;

import com.worldventures.dreamtrips.api.smart_card.user_info.UpdateCardUserHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import rx.Observable;
import rx.schedulers.Schedulers;

class UpdateProfileManager {

   private final Janet janetApi;
   private final SmartCardInteractor interactor;
   private UpdateDataHolder updateDataHolder;

   private String smartCardId;
   private UpdateCardUserData updateCardUserData;

   UpdateProfileManager(Janet janetApi, SmartCardInteractor interactor, UpdateDataHolder updateDataHolder) {
      this.janetApi = janetApi;
      this.interactor = interactor;
      this.updateDataHolder = updateDataHolder;
   }

   void attachChangedFields(ChangedFields changedFields) {
      updateDataHolder.saveChanging(changedFields);
   }

   Observable<SmartCardUser> uploadData(String smartCardId, UpdateCardUserData updateCardUserData) {
      this.smartCardId = smartCardId;
      this.updateCardUserData = updateCardUserData;
      return uploadToServerAndSave();
   }

   Observable<SmartCardUser> retryUploadData() {
      //todo check input data and remove after data successfully uploaded
      return uploadToServerAndSave();
   }

   private Observable<SmartCardUser> uploadToServerAndSave() {
      return janetApi.createPipe(UpdateCardUserHttpAction.class, Schedulers.io())
            .createObservableResult(new UpdateCardUserHttpAction(Long.parseLong(smartCardId), updateCardUserData))
            .map(updateHttpAction -> null)
            .onErrorReturn(throwable -> Observable.error(new UploadProfileDataException(throwable)))
            .flatMap(aVoid -> save());
   }

   private Observable<SmartCardUser> save() {
      return interactor.smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.update(this::bindNewFields))
            .map(Command::getResult);
   }

   private SmartCardUser bindNewFields(SmartCardUser user) {
      final ChangedFields changedFields = updateDataHolder.getChangedFields();
      final ImmutableSmartCardUser.Builder userBuilder = ImmutableSmartCardUser.builder()
            .from(user)
            .firstName(changedFields.firstName())
            .middleName(changedFields.middleName())
            .lastName(changedFields.lastName())
            .phoneNumber(changedFields.phone());

      if (changedFields.photo() != null) {
         userBuilder.userPhoto(ImmutableSmartCardUserPhoto.builder()
               .from(changedFields.photo())
               .photoUrl(updateCardUserData.photoUrl())
               .build());
      }
      return userBuilder.build();
   }
}
