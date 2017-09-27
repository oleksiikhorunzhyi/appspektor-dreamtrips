package com.worldventures.dreamtrips.wallet.service.command.profile;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.util.SmartCardAvatarHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.user.UpdateUserPhotoAction;
import rx.Observable;

import static com.worldventures.dreamtrips.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class UpdateSmartCardUserPhotoCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SmartCardAvatarHelper smartCardAvatarHelper;

   private final String photoUri;

   public UpdateSmartCardUserPhotoCommand(String photoUri) {
      this.photoUri = photoUri;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      smartCardAvatarHelper.toSmartCardPhoto(photoUri)
            .flatMap(this::uploadUserPhotoToSmartCard)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Void> uploadUserPhotoToSmartCard(byte[] bytes) {
      return janet.createPipe(UpdateUserPhotoAction.class)
            .createObservableResult(new UpdateUserPhotoAction(bytes))
            .map(a -> null);
   }
}
