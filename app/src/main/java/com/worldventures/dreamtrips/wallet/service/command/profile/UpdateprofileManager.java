package com.worldventures.dreamtrips.wallet.service.command.profile;

import com.worldventures.dreamtrips.api.smart_card.user_info.UpdateCardUserHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import rx.Observable;

class UpdateProfileManager {

   private final Janet janetApi;
   private final Janet janetWallet;
   private UpdateDataHolder updateDataHolder;

   private SmartCard smartCard;
   private UpdateCardUserData updateCardUserData;

   UpdateProfileManager(Janet janetApi, Janet janetWallet, UpdateDataHolder updateDataHolder) {
      this.janetApi = janetApi;
      this.janetWallet = janetWallet;
      this.updateDataHolder = updateDataHolder;
   }

   Observable<SmartCard> uploadData(SmartCard smartCard, UpdateCardUserData updateCardUserData) {
      this.smartCard = smartCard;
      this.updateCardUserData = updateCardUserData;
      return uploadToServerAndSave();
   }

   Observable<SmartCard> retryUploadData() {
      //todo check input data and remove after data successfully uploaded
      return uploadToServerAndSave();
   }

   private Observable<SmartCard> uploadToServerAndSave() {
      return janetApi.createPipe(UpdateCardUserHttpAction.class)
            .createObservableResult(new UpdateCardUserHttpAction(Long.parseLong(smartCard.smartCardId()), updateCardUserData))
            .map(updateHttpAction -> null)
            .onErrorReturn(throwable -> Observable.error(new UploadProfileDataException(throwable)))
            .flatMap(aVoid -> save());
   }

   private Observable<SmartCard> save() {
      return janetWallet.createPipe(ActiveSmartCardCommand.class)
            .createObservableResult(new ActiveSmartCardCommand(this::bindNewFields))
            .map(Command::getResult);
   }

   private SmartCard bindNewFields(SmartCard smartCard) {
      //// TODO: 12/16/16 check existing getChangedFields before starting upload
      final ChangedFields changedFields = updateDataHolder.getChangedFields();
      final ImmutableSmartCardUser.Builder userBuilder = ImmutableSmartCardUser.builder()
            .from(smartCard.user())
            .firstName(changedFields.firstName())
            .middleName(changedFields.middleName())
            .lastName(changedFields.lastName());

      if (changedFields.photo() != null) {
         userBuilder.userPhoto(ImmutableSmartCardUserPhoto.builder()
               .from(changedFields.photo())
               .photoUrl(updateCardUserData.photoUrl())
               .build());
      }
      return ImmutableSmartCard.builder().from(smartCard)
            .user(userBuilder.build())
            .build();
   }
}
