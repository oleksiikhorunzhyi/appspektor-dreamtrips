package com.worldventures.dreamtrips.wallet.service.command.http;

import android.net.Uri;

import com.worldventures.dreamtrips.api.smart_card.user_info.UpdateCardUserHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableUpdateCardUserData;
import com.worldventures.dreamtrips.core.api.uploadery.SmartCardSimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;

@CommandAction
public class UpdateCardUserServerDataCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_API_LIB) Janet janetApi;
   @Inject Janet janetGeneric;

   private final String firstName;
   private final String middleName;
   private final String lastName;
   private final SmartCardUserPhoto avatar;
   private final String smartCardId;

   public UpdateCardUserServerDataCommand(String firstName, String middleName, String lastName, SmartCardUserPhoto avatar, String smartCardId) {
      this.firstName = firstName;
      this.middleName = middleName;
      this.lastName = lastName;
      this.avatar = avatar;
      this.smartCardId = smartCardId;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      janetGeneric.createPipe(SmartCardSimpleUploaderyCommand.class)
            .createObservableResult(new SmartCardSimpleUploaderyCommand(smartCardId, Uri.fromFile(avatar.original()).toString()))
            .map(c -> c.getResult().response().uploaderyPhoto().location())
            .flatMap(avatarUrl -> {
                     ImmutableUpdateCardUserData cardUserData = ImmutableUpdateCardUserData.builder()
                           .photoUrl(avatarUrl)
                           .firstName(firstName)
                           .middleName(middleName)
                           .lastName(lastName)
                           .build();
                     return janetApi.createPipe(UpdateCardUserHttpAction.class)
                           .createObservableResult(new UpdateCardUserHttpAction(Long.parseLong(smartCardId), cardUserData));
                  }
            ).subscribe(httpAction -> callback.onSuccess(null), callback::onFail);
   }
}
