package com.worldventures.wallet.service.command.settings.general.display;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.domain.WalletConstants;
import com.worldventures.wallet.domain.storage.WalletStorage;
import com.worldventures.wallet.service.command.settings.general.display.exception.MissingUserPhoneException;
import com.worldventures.wallet.service.command.settings.general.display.exception.MissingUserPhotoException;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;
import rx.schedulers.Schedulers;

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class ValidateDisplayTypeDataCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject WalletStorage walletStorage;

   private final boolean hasPhoto;
   private final boolean hasPhone;
   private final boolean forceUpdateDisplayType;

   public ValidateDisplayTypeDataCommand(boolean hasPhoto, boolean hasPhone, boolean forceUpdateDisplayType) {
      this.hasPhoto = hasPhoto;
      this.hasPhone = hasPhone;
      this.forceUpdateDisplayType = forceUpdateDisplayType;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      if (resetToDefault()) {
         janet.createPipe(SetHomeDisplayTypeAction.class, Schedulers.io())
               .createObservableResult(new SetHomeDisplayTypeAction(WalletConstants.SMART_CARD_DEFAULT_DISPLAY_TYPE))
               .doOnNext(action -> walletStorage.setSmartCardDisplayType(action.getType()))
               .map(action -> (Void) null)
               .subscribe(callback::onSuccess, callback::onFail);
      } else {
         callback.onSuccess(null);
      }
   }

   private boolean resetToDefault() {
      final int currentDisplayType = walletStorage.getSmartCardDisplayType(-1);
      switch (currentDisplayType) {
         case SetHomeDisplayTypeAction.DISPLAY_PICTURE_AND_NAME:
         case SetHomeDisplayTypeAction.DISPLAY_PICTURE_ONLY:
            if (!hasPhoto) {
               if (forceUpdateDisplayType) {
                  return true;
               } else {
                  throw new MissingUserPhotoException();
               }
            }
            break;
         case SetHomeDisplayTypeAction.DISPLAY_PHONE_AND_NAME:
            if (!hasPhone) {
               if (forceUpdateDisplayType) {
                  return true;
               } else {
                  throw new MissingUserPhoneException();
               }
            }
            break;
         default:
            break;
      }

      return false;
   }
}
