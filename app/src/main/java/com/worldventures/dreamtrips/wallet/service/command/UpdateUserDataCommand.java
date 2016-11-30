package com.worldventures.dreamtrips.wallet.service.command;


import android.net.Uri;
import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.api.smart_card.user_info.UpdateCardUserHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableUpdateCardUserData;
import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;
import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class UpdateUserDataCommand extends Command<SmartCard> implements InjectableAction {

   @Inject WizardInteractor wizardInteractor;
   @Inject @Named(JANET_WALLET) Janet jannetWallet;
   @Inject @Named(JANET_API_LIB) Janet janetApi;
   @Inject Janet jannetGeneric;
   @Inject MapperyContext mappery;

   private final SmartCardUser baseUser;
   private final String firstName;
   private final String middleName;
   private final String lastName;
   private final SmartCardUserPhoto avatar;
   private final String smartCardId;

   public UpdateUserDataCommand(SmartCardUser baseUser, String firstName, String middleName, String lastName, SmartCardUserPhoto avatar, String smartCardId) {
      this.baseUser = baseUser;
      this.firstName = firstName;
      this.middleName = middleName;
      this.lastName = lastName;
      this.avatar = avatar;
      this.smartCardId = smartCardId;
   }

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      final ActionPipe<SetupUserDataCommand> pipe = jannetWallet.createPipe(SetupUserDataCommand.class);
      pipe.createObservableResult(new SetupUserDataCommand(firstName, middleName, lastName, avatar, smartCardId))
            .flatMap(it -> uploadUserData()
                  .doOnError(e -> pipe.send(restoreCommand()))
                  .map(a -> it.getResult()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @NonNull
   private SetupUserDataCommand restoreCommand() {
      return new SetupUserDataCommand(baseUser.fullName(), baseUser.middleName(), baseUser.lastName(), avatar, smartCardId);
   }

   private Observable<? extends UpdateCardUserHttpAction> uploadUserData() {
      return jannetGeneric.createPipe(SimpleUploaderyCommand.class)
            .createObservableResult(new SimpleUploaderyCommand(Uri.fromFile(avatar.original()).toString()))
            .map(c -> c.getResult().getPhotoUploadResponse().getLocation())
            .flatMap(avatarUrl -> {
                     ImmutableUpdateCardUserData cardUserData = ImmutableUpdateCardUserData.builder()
                           .nameToDisplay(firstName/*TODO when server changes is, return as is*/)
                           .photoUrl(avatarUrl)
                           .build();
                     return janetApi.createPipe(UpdateCardUserHttpAction.class)
                           .createObservableResult(new UpdateCardUserHttpAction(Long.parseLong(smartCardId), cardUserData));
                  }
            );
   }

}
