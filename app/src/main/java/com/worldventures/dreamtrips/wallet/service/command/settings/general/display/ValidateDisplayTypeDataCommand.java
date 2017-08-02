package com.worldventures.dreamtrips.wallet.service.command.settings.general.display;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.WalletConstants;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.exception.MissingUserPhoneException;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.exception.MissingUserPhotoException;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;
import rx.schedulers.Schedulers;

@CommandAction
public class ValidateDisplayTypeDataCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
   @Inject SnappyRepository snappyRepository;

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
               .doOnNext(action -> snappyRepository.setSmartCardDisplayType(action.getType()))
               .map(action -> (Void) null)
               .subscribe(callback::onSuccess, callback::onFail);
      } else {
         callback.onSuccess(null);
      }
   }

   private boolean resetToDefault() {
      final int currentDisplayType = snappyRepository.getSmartCardDisplayType(-1);
      switch (currentDisplayType) {
         case SetHomeDisplayTypeAction.DISPLAY_PICTURE_AND_NAME:
         case SetHomeDisplayTypeAction.DISPLAY_PICTURE_ONLY:
            if (!hasPhoto) {
               if (forceUpdateDisplayType) {return true;} else {throw new MissingUserPhotoException();}
            }
            break;
         case SetHomeDisplayTypeAction.DISPLAY_PHONE_AND_NAME:
            if (!hasPhone) {
               if (forceUpdateDisplayType) {return true;} else {throw new MissingUserPhoneException();}
            }
            break;
      }

      return false;
   }
}
