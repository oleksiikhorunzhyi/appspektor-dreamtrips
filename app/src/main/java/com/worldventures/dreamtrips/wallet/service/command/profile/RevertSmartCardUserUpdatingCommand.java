package com.worldventures.dreamtrips.wallet.service.command.profile;

import android.support.v4.util.Pair;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;
import com.worldventures.dreamtrips.util.SmartCardAvatarHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;

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
      if (changedFields.photo() == null) return Observable.just(null);
      return Observable
            .fromCallable(() -> getAvatarAsByteArray(user.userPhoto().original()))
            .flatMap(bytes ->  janet.createPipe(UpdateUserPhotoAction.class)
                  .createObservableResult(new UpdateUserPhotoAction(bytes)))
            .map(action -> null);
   }

   private byte[] getAvatarAsByteArray(File originalFile) throws IOException {
      final int[][] ditheredImageArray =
            smartCardAvatarHelper.toMonochrome(originalFile, ImageUtils.DEFAULT_IMAGE_SIZE);
      return smartCardAvatarHelper.convertBytesForUpload(ditheredImageArray);
   }

   private User createUser(SmartCardUser user, String smartCardId) {
      return ImmutableUser.builder()
            .firstName(user.firstName())
            .middleName(user.middleName())
            .lastName(user.lastName())
            .isUserAssigned(true)
            .memberId(userSessionHolder.get().get().getUser().getId())
            .barcodeId(Long.parseLong(smartCardId))
            .memberStatus(UserSmartCardUtils.obtainMemberStatus(userSessionHolder))
            .build();
   }
}
