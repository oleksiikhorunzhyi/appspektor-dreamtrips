package com.worldventures.dreamtrips.wallet.service.command.profile;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.util.SmartCardAvatarHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.user.UpdateUserAction;
import io.techery.janet.smartcard.action.user.UpdateUserPhotoAction;
import io.techery.janet.smartcard.model.ImmutableUser;
import io.techery.janet.smartcard.model.User;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class RevertSmartCardUserUpdatingCommand extends Command<Void> {

   @Inject UpdateDataHolder updateDataHolder;
   @Inject @Named(JANET_WALLET) Janet janet;

   @Inject SessionHolder<UserSession> userSessionHolder;
   @Inject SmartCardAvatarHelper smartCardAvatarHelper;

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(ActiveSmartCardCommand.class)
            .createObservableResult(new ActiveSmartCardCommand())
            .flatMap(command -> revertUpdating(command.getResult()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Void> revertUpdating(SmartCard smartCard) {
      final ChangedFields changedFields = updateDataHolder.getChangedFields();
      updateDataHolder.clear();
      return revertName(changedFields, smartCard)
            .flatMap(aVoid -> revertPhoto(changedFields, smartCard));
   }

   private Observable<Void> revertName(ChangedFields changedFields, SmartCard smartCard) {
      final SmartCardUser user = smartCard.user();
      final boolean nameChanged = changedFields.firstName().equals(user.firstName()) &&
            changedFields.middleName().equals(user.middleName()) &&
            changedFields.lastName().equals(user.lastName());
      if (nameChanged) {
         return janet.createPipe(UpdateUserAction.class)
               .createObservableResult(new UpdateUserAction(createUser(smartCard)))
               .map(action -> null);
      } else {
         return Observable.just(null);
      }
   }

   private Observable<Void> revertPhoto(ChangedFields changedFields, SmartCard smartCard) {
      if (changedFields.photo() == null) return Observable.just(null);

      return janet.createPipe(UpdateUserPhotoAction.class)
            .createObservableResult(new UpdateUserPhotoAction(smartCardAvatarHelper
                  .convertBytesForUpload(smartCard.user().userPhoto().monochrome()))
            )
            .map(action -> null);
   }

   private User createUser(SmartCard smartCard) {
      final SmartCardUser user = smartCard.user();
      return ImmutableUser.builder()
            .firstName(user.firstName())
            .middleName(user.middleName())
            .lastName(user.lastName())
            .isUserAssigned(true)
            .memberId(userSessionHolder.get().get().getUser().getId())
            .barcodeId(Long.parseLong(smartCard.smartCardId()))
            .memberStatus(UserSmartCardUtils.obtainMemberStatus(userSessionHolder))
            .build();
   }
}
