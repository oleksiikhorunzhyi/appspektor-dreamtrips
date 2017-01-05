package com.worldventures.dreamtrips.wallet.service.command;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.util.SmartCardAvatarHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.command.profile.UserSmartCardUtils;
import com.worldventures.dreamtrips.wallet.service.storage.WizardMemoryStorage;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.user.AssignUserAction;
import io.techery.janet.smartcard.action.user.UpdateUserPhotoAction;
import io.techery.janet.smartcard.model.ImmutableUser;
import io.techery.janet.smartcard.model.User;
import io.techery.mappery.MapperyContext;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class SetupUserDataCommand extends Command<SmartCard> implements InjectableAction {

   @Inject Janet janetGeneric;
   @Inject @Named(JANET_WALLET) Janet janetWallet;
   @Inject SessionHolder<UserSession> userSessionHolder;
   @Inject SmartCardAvatarHelper smartCardAvatarHelper;
   @Inject WizardMemoryStorage wizardMemoryStorage;
   @Inject MapperyContext mappery;

   private final String firstName;
   private final String middleName;
   private final String lastName;
   private final String barcode;
   private final SmartCardUserPhoto avatar;

   private SmartCard smartCard;

   public SetupUserDataCommand(String fullName, SmartCardUserPhoto avatar, String barcode, SmartCard smartCard) {
      this(split(fullName)[0], split(fullName)[1], split(fullName)[2], avatar, barcode, smartCard);
   }

   /**
    * barcode in constructor because
    * SetupUserDataCommand#getCacheOptions() requires barcode from WizardMemoryStorage
    * and getCacheOptions executes before inject
    */
   public SetupUserDataCommand(String firstName, String middleName, String lastName, SmartCardUserPhoto avatar, String barcode, SmartCard smartCard) {
      this.firstName = firstName;
      this.middleName = middleName;
      this.lastName = lastName;
      this.avatar = avatar;
      this.barcode = barcode;
      this.smartCard = smartCard;
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
            .subscribe(smartCard -> {
               wizardMemoryStorage.saveUserPhoto(avatar.original());
               wizardMemoryStorage.saveName(firstName, middleName, lastName);
               callback.onSuccess(smartCard);
            }, callback::onFail);
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
      if (avatar.monochrome() == null || avatar.original() == null)
         throw new MissedAvatarException("Monochrome avatar file == null");
      if (!avatar.monochrome().exists() || !avatar.original().exists())
         throw new MissedAvatarException("Avatar does not exist");


      WalletValidateHelper.validateUserFullNameOrThrow(firstName, middleName, lastName);

      return ImmutableUser.builder()
            .firstName(firstName)
            .lastName(lastName)
            .middleName(middleName)
            .memberStatus(UserSmartCardUtils.obtainMemberStatus(userSessionHolder))
            .memberId(userSessionHolder.get().get().getUser().getId())
            .barcodeId(Long.valueOf(wizardMemoryStorage.getBarcode()))
            .build();
   }


   private byte[] getAvatarAsByteArray() throws IOException {
      return smartCardAvatarHelper.convertBytesForUpload(avatar.monochrome());
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

      MissedAvatarException(String message) {
         super(message);
      }
   }
}
