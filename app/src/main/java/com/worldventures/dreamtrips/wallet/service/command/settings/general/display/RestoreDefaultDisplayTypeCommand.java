package com.worldventures.dreamtrips.wallet.service.command.settings.general.display;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.WalletConstants;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;
import rx.schedulers.Schedulers;

@CommandAction
public class RestoreDefaultDisplayTypeCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
   @Inject SnappyRepository snappyRepository;

   private final SmartCardUser user;

   public RestoreDefaultDisplayTypeCommand(SmartCardUser user) {
      this.user = user;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      int displayType = snappyRepository.getSmartCardDisplayType(-1);
      if (displayType < 0) {
         displayType = getDefaultDisplayType();

         janet.createPipe(SetHomeDisplayTypeAction.class, Schedulers.io())
               .createObservableResult(new SetHomeDisplayTypeAction(displayType))
               .doOnNext(action -> snappyRepository.setSmartCardDisplayType(action.getType()))
               .map(action -> (Void) null)
               .subscribe(callback::onSuccess, callback::onFail);
      } else {
         callback.onSuccess(null);
      }
   }

   private int getDefaultDisplayType() {
      int displayType = WalletConstants.SMART_CARD_DEFAULT_DISPLAY_TYPE;

      if (user.userPhoto() != null) displayType = SetHomeDisplayTypeAction.DISPLAY_PICTURE_AND_NAME;
      else if (user.phoneNumber() != null) displayType = SetHomeDisplayTypeAction.DISPLAY_PHONE_AND_NAME;

      return displayType;
   }
}
