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
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardStorage;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

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
import io.techery.mappery.MapperyContext;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;
import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class SetupUserDataCommand extends Command<SmartCard> implements InjectableAction, CachedAction<SmartCard>, SmartCardModifier {

   @Inject Janet janetGeneric;
   @Inject @Named(JANET_WALLET) Janet janetWallet;
   @Inject @Named(JANET_API_LIB) Janet janetApi;
   @Inject SessionHolder<UserSession> userSessionHolder;
   @Inject SmartCardAvatarHelper smartCardAvatarHelper;
   @Inject MapperyContext mappery;

   private final String firstName;
   private final String middleName;
   private final String lastName;
   private final SmartCardUserPhoto avatar;
   private final String smartCardId;

   private SmartCard smartCard;

   public SetupUserDataCommand(String fullName, SmartCardUserPhoto avatar, String smartCardId) {
      this(split(fullName)[0], split(fullName)[1], split(fullName)[2], avatar, smartCardId);
   }

   public SetupUserDataCommand(String firstName, String middleName, String lastName, SmartCardUserPhoto avatar, String smartCardId) {
      this.firstName = firstName;
      this.middleName = middleName;
      this.lastName = lastName;
      this.avatar = avatar;
      this.smartCardId = smartCardId;
   }

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      User user = validateUserNameAndCreateUser();
      janetWallet
            .createPipe(AssignUserAction.class).createObservableResult(new AssignUserAction(user))
            .flatMap(action -> Observable
                  .fromCallable(this::getAvatarAsByteArray)
                  .flatMap(bytesArray -> janetWallet.createPipe(UpdateUserPhotoAction.class)
                        .createObservableResult(new UpdateUserPhotoAction(bytesArray)))
            )
            .map(action -> attachAvatarToLocalSmartCard(user))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private SmartCard attachAvatarToLocalSmartCard(User user) {
      smartCard = ImmutableSmartCard.builder()
            .from(smartCard)
            .user(ImmutableSmartCardUser.builder()
                  .from(mappery.convert(user, SmartCardUser.class))
                  .userPhoto(avatar)
                  .build())
            .build();
      return smartCard;
   }

   private User validateUserNameAndCreateUser() throws FormatException {
      if (avatar == null) throw new MissedAvatarException("avatar == null");
      if (avatar.monochrome() == null) throw new MissedAvatarException("Monochrome avatar file == null");
      if (!avatar.monochrome().exists()) throw new MissedAvatarException("Avatar does not exist");


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
      return smartCardAvatarHelper.convertBytesForUpload(avatar.monochrome());
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

   private static String[] split(String fullName) {
      String[] nameParts = fullName.split(" ");
      String firstName = null, lastName = null, middleName = null;
      if (nameParts.length == 2) {
         firstName = nameParts[0];
         lastName = nameParts[1];
      } else if (nameParts.length == 3) {
         firstName = nameParts[0];
         middleName = nameParts[1];
         lastName = nameParts[2];
      }
      return new String[]{firstName, middleName, lastName};
   }

   public static class MissedAvatarException extends RuntimeException {

      public MissedAvatarException(String message) {
         super(message);
      }
   }
}
