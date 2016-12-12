package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardAvatarCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.UpdateCardUserServerDataCommand;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@Singleton
public class SmartCardUserDataInteractor {

   private final ActionPipe<SmartCardAvatarCommand> smartCardAvatarPipe;
   private final ActionPipe<SetupUserDataCommand> setupUserDataCommandPipe;
   private final ActionPipe<UpdateCardUserServerDataCommand> cardUserServerDataCommandPipe;

   @Inject public SmartCardUserDataInteractor(@Named(JANET_WALLET) Janet janet) {
      smartCardAvatarPipe = janet.createPipe(SmartCardAvatarCommand.class, Schedulers.io());
      setupUserDataCommandPipe = janet.createPipe(SetupUserDataCommand.class, Schedulers.io());
      cardUserServerDataCommandPipe = janet.createPipe(UpdateCardUserServerDataCommand.class, Schedulers.io());
   }

   public ActionPipe<SmartCardAvatarCommand> smartCardAvatarPipe() {
      return smartCardAvatarPipe;
   }

   public ActionPipe<SetupUserDataCommand> setupUserDataCommandPipe() {
      return setupUserDataCommandPipe;
   }

   public ActionPipe<UpdateCardUserServerDataCommand> updateCardUserServerDataCommandPipe() {
      return cardUserServerDataCommandPipe;
   }

}
