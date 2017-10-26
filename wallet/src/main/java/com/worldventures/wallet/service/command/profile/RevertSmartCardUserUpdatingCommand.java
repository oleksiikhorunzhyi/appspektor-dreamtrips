package com.worldventures.wallet.service.command.profile;

import android.support.v4.util.Pair;

import com.worldventures.wallet.domain.entity.SmartCard;
import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.wallet.service.WalletSocialInfoProvider;
import com.worldventures.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.wallet.service.command.SmartCardUserCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.user.UpdateUserAction;
import io.techery.janet.smartcard.model.ImmutableUser;
import io.techery.janet.smartcard.model.User;
import rx.Observable;

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class RevertSmartCardUserUpdatingCommand extends Command<Void> {

   @Inject UpdateDataHolder updateDataHolder;
   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject WalletSocialInfoProvider socialInfoProvider;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      Observable.zip(
            janet.createPipe(ActiveSmartCardCommand.class)
                  .createObservableResult(new ActiveSmartCardCommand()),
            janet.createPipe(SmartCardUserCommand.class)
                  .createObservableResult(SmartCardUserCommand.fetch()), Pair::new
      )
            .flatMap(pair -> revertUpdating(pair.first.getResult(), pair.second.getResult()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Void> revertUpdating(SmartCard smartCard, SmartCardUser user) {
      final ChangedFields changedFields = updateDataHolder.getChangedFields();
      updateDataHolder.clear();
      return revertName(changedFields, user, smartCard.smartCardId())
            .flatMap(aVoid -> revertPhoto(changedFields, user));
   }

   private Observable<Void> revertName(ChangedFields changedFields, SmartCardUser user, String smartCardId) {
      final boolean nameChanged = changedFields.firstName().equals(user.firstName()) &&
            changedFields.middleName().equals(user.middleName()) &&
            changedFields.lastName().equals(user.lastName());
      if (nameChanged) {
         return janet.createPipe(UpdateUserAction.class)
               .createObservableResult(new UpdateUserAction(createUser(user, smartCardId)))
               .map(action -> null);
      } else {
         return Observable.just(null);
      }
   }

   private Observable<Void> revertPhoto(ChangedFields changedFields, SmartCardUser user) {
      final SmartCardUserPhoto userPhoto = user.userPhoto();
      if (changedFields.photo() == null || userPhoto == null) return Observable.just(null);
      return janet.createPipe(UpdateSmartCardUserPhotoCommand.class)
            .createObservableResult(new UpdateSmartCardUserPhotoCommand(userPhoto.uri()))
            .map(action -> null);
   }

   private User createUser(SmartCardUser user, String smartCardId) {
      return ImmutableUser.builder()
            .firstName(user.firstName())
            .middleName(user.middleName())
            .lastName(user.lastName())
            .isUserAssigned(true)
            .memberId(socialInfoProvider.userId())
            .barcodeId(Long.parseLong(smartCardId))
            .memberStatus(socialInfoProvider.memberStatus())
            .build();
   }
}
