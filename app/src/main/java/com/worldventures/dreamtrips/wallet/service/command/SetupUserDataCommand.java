package com.worldventures.dreamtrips.wallet.service.command;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.util.SmartCardAvatarHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardStorage;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.user.AssignUserAction;
import io.techery.janet.smartcard.action.user.UpdateUserPhotoAction;
import io.techery.janet.smartcard.model.ImmutableUser;
import io.techery.janet.smartcard.model.User;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class SetupUserDataCommand extends Command<SmartCard> implements InjectableAction, CachedAction<SmartCard> {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SessionHolder<UserSession> userSessionHolder;
   @Inject SmartCardAvatarHelper smartCardAvatarHelper;

   private final String fullName;
   private final File avatarFile;
   private final String smartCardId;
   private SmartCard smartCard;

   public SetupUserDataCommand(String fullName, File avatarFile, String smartCardId) {
      // TODO: 8/2/16 change on first name and second name
      this.fullName = fullName;
      this.avatarFile = avatarFile;
      this.smartCardId = smartCardId;
   }

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      User user = validateUserNameAndCreateUser();
      janet.createPipe(AssignUserAction.class)
            .createObservableResult(new AssignUserAction(user))
            .flatMap(action -> Observable.fromCallable(this::getAvatarAsByteArray))
            .flatMap(bytesArray -> janet.createPipe(UpdateUserPhotoAction.class)
                  .createObservableResult(new UpdateUserPhotoAction(bytesArray)))
            .doOnNext(updateUserPhotoAction -> {
               //todo send update user info & photo to origin server AssociateCardUser
            })
            .map(action -> attachAvatarToLocalSmartCard())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private SmartCard attachAvatarToLocalSmartCard() {
      smartCard = ImmutableSmartCard.builder()
            .from(smartCard)
            .userPhoto("file://" + avatarFile.getAbsolutePath())
            .cardName(fullName)
            .build();
      return smartCard;
   }

   private User validateUserNameAndCreateUser() throws FormatException {
      if (avatarFile == null) throw new MissedAvatarException("avatarFile == null");
      if (!avatarFile.exists()) throw new MissedAvatarException("Avatar does not exist");

      String[] nameParts = fullName.split(" ");
      String firstName, lastName, middleName = null;
      if (nameParts.length < 2 || nameParts.length > 3) throw new FormatException();
      if (nameParts.length == 2) {
         firstName = nameParts[0];
         lastName = nameParts[1];
      } else {
         firstName = nameParts[0];
         middleName = nameParts[1];
         lastName = nameParts[2];
      }
      WalletValidateHelper.validateUserFullNameOrThrow(firstName, middleName, lastName);

      return ImmutableUser.builder()
            .firstName(firstName)
            .lastName(lastName)
            .middleName(middleName)
            .memberStatus(getMemberStatus())
            .memberId(userSessionHolder.get().get().getUser().getId())
            .barcodeId(Long.valueOf(smartCardId))
            .build();
   }

   private User.MemberStatus getMemberStatus() {
      com.worldventures.dreamtrips.modules.common.model.User user = userSessionHolder.get().get().getUser();
      if (user.isGold()) return User.MemberStatus.GOLD;
      if (user.isGeneral() || user.isPlatinum()) return User.MemberStatus.ACTIVE;
      return User.MemberStatus.INACTIVE;
   }

   private byte[] getAvatarAsByteArray() throws IOException {
      return smartCardAvatarHelper.convertBytesForUpload(avatarFile);
   }

   @Override
   public SmartCard getCacheData() {
      return smartCard;
   }

   @Override
   public void onRestore(ActionHolder holder, SmartCard cache) {
      this.smartCard = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundle bundle = new CacheBundleImpl();
      bundle.put(SmartCardStorage.CARD_ID_PARAM, smartCardId);

      return ImmutableCacheOptions.builder()
            .params(bundle)
            .restoreFromCache(true)
            .saveToCache(true)
            .build();
   }

   public static class MissedAvatarException extends RuntimeException {

      public MissedAvatarException(String message) {
         super(message);
      }
   }
}
