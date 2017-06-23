package com.worldventures.dreamtrips.wallet.service.command.settings.general.display;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhone;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;
import rx.schedulers.Schedulers;
import rx.Observable;

import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_PHONE_AND_NAME;
import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_PICTURE_AND_NAME;
import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.DISPLAY_PICTURE_ONLY;

@CommandAction
public class SaveDisplayTypeCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject SnappyRepository snappyRepository;

   @SetHomeDisplayTypeAction.HomeDisplayType
   private final int displayType;
   private final SmartCardUser smartCardUser;

   public SaveDisplayTypeCommand(SmartCardUser smartCardUser, @SetHomeDisplayTypeAction.HomeDisplayType int type) {
      this.smartCardUser = smartCardUser;
      this.displayType = type;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      Observable.just(smartCardUser)
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
               }
            })
            .flatMap(smartCardUser -> janet.createPipe(SetHomeDisplayTypeAction.class, Schedulers.io())
                  .createObservableResult(new SetHomeDisplayTypeAction(displayType)))
            .doOnNext(action -> snappyRepository.setSmartCardDisplayType(action.getType()))
            .map(action -> (Void) null)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   public static class MissingUserPhoneException extends NullPointerException {
      MissingUserPhoneException() {
         super("User must have a phone number in order to use chosen display option");
      }
   }

   public static class MissingUserPhotoException extends NullPointerException {
      MissingUserPhotoException() {
         super("User must have a photo in order to use chosen display option");
      }
   }
}