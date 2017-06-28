package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.GetSmartCardStatusCommand;
import com.worldventures.dreamtrips.wallet.service.command.wizard.ReAssignCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.wizard.WizardCheckCommand;
import com.worldventures.dreamtrips.wallet.service.command.wizard.WizardCompleteCommand;
import com.worldventures.dreamtrips.wallet.service.provisioning.PinOptionalCommand;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningModeCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.ReadActionPipe;
import io.techery.janet.smartcard.action.settings.StartPinSetupAction;
import io.techery.janet.smartcard.event.PinSetupFinishedEvent;
import rx.schedulers.Schedulers;

public final class WizardInteractor {

   private final ActionPipe<CreateAndConnectToCardCommand> createAndConnectPipe;
   private final ActionPipe<SetupUserDataCommand> setupUserDataPipe;

   private final ReadActionPipe<PinSetupFinishedEvent> pinSetupFinishedPipe;
   private final ActionPipe<StartPinSetupAction> startPinSetupPipe;
   private final ActionPipe<ActivateSmartCardCommand> activateSmartCardPipe;
   private final ActionPipe<WizardCheckCommand> checksPipe;
   private final ActionPipe<GetSmartCardStatusCommand> getSmartCardStatusCommandActionPipe;

   private final ActionPipe<ReAssignCardCommand> reAssignCardPipe;

   private final ActionPipe<WizardCompleteCommand> completePipe;
   private final ActionPipe<ProvisioningModeCommand> provisioningStatePipe;
   private final ActionPipe<PinOptionalCommand> pinOptionalActionPipe;

   public WizardInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      createAndConnectPipe = sessionActionPipeCreator.createPipe(CreateAndConnectToCardCommand.class, Schedulers.io());
      setupUserDataPipe = sessionActionPipeCreator.createPipe(SetupUserDataCommand.class, Schedulers.io());
      activateSmartCardPipe = sessionActionPipeCreator.createPipe(ActivateSmartCardCommand.class, Schedulers.io());

      pinSetupFinishedPipe = sessionActionPipeCreator.createPipe(PinSetupFinishedEvent.class, Schedulers.io());
      startPinSetupPipe = sessionActionPipeCreator.createPipe(StartPinSetupAction.class, Schedulers.io());
      checksPipe = sessionActionPipeCreator.createPipe(WizardCheckCommand.class, Schedulers.io());

      reAssignCardPipe = sessionActionPipeCreator.createPipe(ReAssignCardCommand.class, Schedulers.io());
      getSmartCardStatusCommandActionPipe = sessionActionPipeCreator.createPipe(GetSmartCardStatusCommand.class, Schedulers
            .io());

      completePipe = sessionActionPipeCreator.createPipe(WizardCompleteCommand.class, Schedulers.io());
      provisioningStatePipe = sessionActionPipeCreator.createPipe(ProvisioningModeCommand.class, Schedulers.io());
      pinOptionalActionPipe = sessionActionPipeCreator.createPipe(PinOptionalCommand.class, Schedulers.io());
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

   public ActionPipe<ReAssignCardCommand> reAssignCardPipe() {
      return reAssignCardPipe;
   }

   public ActionPipe<WizardCheckCommand> checksPipe() {
      return checksPipe;
   }

   public ActionPipe<WizardCompleteCommand> completePipe() {
      return completePipe;
   }

   public ActionPipe<GetSmartCardStatusCommand> getSmartCardStatusCommandActionPipe() {
      return getSmartCardStatusCommandActionPipe;
   }

   public ActionPipe<ProvisioningModeCommand> provisioningStatePipe() {
      return provisioningStatePipe;
   }

   public ActionPipe<PinOptionalCommand> pinOptionalActionPipe() {
      return pinOptionalActionPipe;
   }
}
