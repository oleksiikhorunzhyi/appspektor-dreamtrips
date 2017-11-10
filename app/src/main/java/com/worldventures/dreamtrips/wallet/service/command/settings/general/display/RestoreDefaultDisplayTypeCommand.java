package com.worldventures.dreamtrips.wallet.service.command.settings.general.display;

import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.WalletConstants;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.storage.WalletStorage;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class RestoreDefaultDisplayTypeCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject WalletStorage walletStorage;

   private final boolean hasPhoto;
   private final boolean hasPhone;

   public RestoreDefaultDisplayTypeCommand(SmartCardUser user) {
      hasPhoto = user.userPhoto() != null;
      hasPhone = user.phoneNumber() != null;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      int displayType = walletStorage.getSmartCardDisplayType(-1);
      if (displayType < 0) {
         displayType = getDefaultDisplayType();

         janet.createPipe(SetHomeDisplayTypeAction.class, Schedulers.io())
               .createObservableResult(new SetHomeDisplayTypeAction(displayType))
               .doOnNext(action -> walletStorage.setSmartCardDisplayType(action.getType()))
               .map(action -> (Void) null)
               .subscribe(callback::onSuccess, callback::onFail);
      } else {
         callback.onSuccess(null);
      }
   }

   private int getDefaultDisplayType() {
      int displayType = WalletConstants.SMART_CARD_DEFAULT_DISPLAY_TYPE;

      if (hasPhoto) displayType = SetHomeDisplayTypeAction.DISPLAY_PICTURE_AND_NAME;
      else if (hasPhone) displayType = SetHomeDisplayTypeAction.DISPLAY_PHONE_AND_NAME;

      return displayType;
   }
}