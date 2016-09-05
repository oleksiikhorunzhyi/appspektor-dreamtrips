package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.ReadActionPipe;
import io.techery.janet.smartcard.action.settings.StartPinSetupAction;
import io.techery.janet.smartcard.event.PinSetupFinishedEvent;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

public final class WizardInteractor {
   private final ActionPipe<CreateAndConnectToCardCommand> createAndConnectPipe;
   private final ActionPipe<SetupUserDataCommand> setupUserDataPipe;

   private final ReadActionPipe<PinSetupFinishedEvent> pinSetupFinishedPipe;
   private final ActionPipe<StartPinSetupAction> startPinSetupPipe;
   private final ActionPipe<ActivateSmartCardCommand> activateSmartCardPipe;

   @Inject
   public WizardInteractor(@Named(JANET_WALLET) Janet janet) {
      createAndConnectPipe = janet.createPipe(CreateAndConnectToCardCommand.class, Schedulers.io());
      setupUserDataPipe = janet.createPipe(SetupUserDataCommand.class, Schedulers.io());
      activateSmartCardPipe = janet.createPipe(ActivateSmartCardCommand.class, Schedulers.io());

      pinSetupFinishedPipe = janet.createPipe(PinSetupFinishedEvent.class, Schedulers.io());
      startPinSetupPipe = janet.createPipe(StartPinSetupAction.class, Schedulers.io());
   }

   public ActionPipe<CreateAndConnectToCardCommand> createAndConnectActionPipe() {
      return createAndConnectPipe;
   }

   public ActionPipe<SetupUserDataCommand> setupUserDataPipe() {
      return setupUserDataPipe;
   }

   public ActionPipe<StartPinSetupAction> startPinSetupPipe() {
      return startPinSetupPipe;
   }

   public ReadActionPipe<PinSetupFinishedEvent> pinSetupFinishedPipe() {
      return pinSetupFinishedPipe;
   }

   public ActionPipe<ActivateSmartCardCommand> activateSmartCardPipe() {
      return activateSmartCardPipe;
   }
}
