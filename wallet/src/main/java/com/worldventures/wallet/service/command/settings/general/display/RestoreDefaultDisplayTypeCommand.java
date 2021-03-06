package com.worldventures.wallet.service.command.settings.general.display;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.domain.WalletConstants;
import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.domain.storage.WalletStorage;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.wallet.util.SCFirmwareUtils;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class RestoreDefaultDisplayTypeCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject WalletStorage walletStorage;
   @Inject SmartCardInteractor smartCardInteractor;

   private final boolean hasPhoto;
   private final boolean hasPhone;

   public RestoreDefaultDisplayTypeCommand(SmartCardUser user) {
      hasPhoto = user.getUserPhoto() != null;
      hasPhone = user.getPhoneNumber() != null;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      int displayType = walletStorage.getSmartCardDisplayType(-1);
      if (displayType < 0) {
         final int newDisplayType = getDefaultDisplayType();
         smartCardInteractor.deviceStatePipe()
               .createObservableResult(DeviceStateCommand.Companion.fetch())
               .flatMap(command -> {
                  if (SCFirmwareUtils.supportHomeDisplayOptions(command.getResult().getFirmwareVersion())) {
                     return janet.createPipe(SetHomeDisplayTypeAction.class, Schedulers.io())
                           .createObservableResult(new SetHomeDisplayTypeAction(newDisplayType))
                           .map(SetHomeDisplayTypeAction::getType);
                  }
                  return Observable.just(SetHomeDisplayTypeAction.DISPLAY_PICTURE_ONLY);
               })

               .doOnNext(type -> walletStorage.setSmartCardDisplayType(type))
               .map(action -> (Void) null)
               .subscribe(callback::onSuccess, callback::onFail);
      } else {
         callback.onSuccess(null);
      }
   }

   private int getDefaultDisplayType() {
      int displayType = WalletConstants.SMART_CARD_DEFAULT_DISPLAY_TYPE;

      if (hasPhoto) {
         displayType = SetHomeDisplayTypeAction.DISPLAY_PICTURE_AND_NAME;
      } else if (hasPhone) {
         displayType = SetHomeDisplayTypeAction.DISPLAY_PHONE_AND_NAME;
      }

      return displayType;
   }
}
