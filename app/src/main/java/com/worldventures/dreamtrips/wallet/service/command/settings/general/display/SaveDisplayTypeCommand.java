package com.worldventures.dreamtrips.wallet.service.command.settings.general.display;

import android.support.annotation.NonNull;

import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhone;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.domain.storage.WalletStorage;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.exception.MissingUserPhoneException;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.exception.MissingUserPhotoException;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.wallet.di.WalletJanetModule.JANET_WALLET;
import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_PHONE_AND_NAME;
import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_PICTURE_AND_NAME;
import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_PICTURE_ONLY;

@CommandAction
public class SaveDisplayTypeCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject WalletStorage walletStorage;

   @SetHomeDisplayTypeAction.HomeDisplayType
   private final int displayType;
   private final SmartCardUser user;

   public SaveDisplayTypeCommand(@SetHomeDisplayTypeAction.HomeDisplayType int type, @NonNull SmartCardUser user) {
      this.displayType = type;
      this.user = user;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      Observable.just(user)
            .doOnNext(smartCardUser -> {
               if (smartCardUser == null) throw new NullPointerException("User cannot be null at this point");

               switch (displayType) {
                  case DISPLAY_PHONE_AND_NAME:
                     final SmartCardUserPhone userPhone = smartCardUser.phoneNumber();
                     if (userPhone == null || ProjectTextUtils.isEmpty(userPhone.number())) {
                        throw new MissingUserPhoneException();
                     }
                     break;
                  case DISPLAY_PICTURE_AND_NAME:
                  case DISPLAY_PICTURE_ONLY:
                     final SmartCardUserPhoto userPhoto = smartCardUser.userPhoto();
                     if (userPhoto == null) {
                        throw new MissingUserPhotoException();
                     }
                     break;
                  default:
                     break;
               }
            })
            .flatMap(smartCardUser -> janet.createPipe(SetHomeDisplayTypeAction.class, Schedulers.io())
                  .createObservableResult(new SetHomeDisplayTypeAction(displayType)))
            .doOnNext(action -> walletStorage.setSmartCardDisplayType(action.getType()))
            .map(action -> (Void) null)
            .subscribe(callback::onSuccess, callback::onFail);
   }

}
