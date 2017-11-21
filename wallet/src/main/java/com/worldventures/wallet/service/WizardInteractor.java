package com.worldventures.wallet.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.wallet.service.command.SetupUserDataCommand;
import com.worldventures.wallet.service.command.http.FetchTermsAndConditionsCommand;
import com.worldventures.wallet.service.command.http.GetSmartCardStatusCommand;
import com.worldventures.wallet.service.command.wizard.ReAssignCardCommand;
import com.worldventures.wallet.service.command.wizard.WizardCompleteCommand;
import com.worldventures.wallet.service.provisioning.PinOptionalCommand;
import com.worldventures.wallet.service.provisioning.ProvisioningModeCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.ReadActionPipe;
import io.techery.janet.smartcard.action.settings.CancelPinSetupAction;
import io.techery.janet.smartcard.action.settings.StartPinSetupAction;
import io.techery.janet.smartcard.event.PinSetupFinishedEvent;
import rx.schedulers.Schedulers;

public final class WizardInteractor {

   private final ActionPipe<CreateAndConnectToCardCommand> createAndConnectPipe;
   private final ActionPipe<SetupUserDataCommand> setupUserDataPipe;

   private final ReadActionPipe<PinSetupFinishedEvent> pinSetupFinishedPipe;
   private final ActionPipe<StartPinSetupAction> startPinSetupPipe;
   private final ActionPipe<CancelPinSetupAction> cancelPinSetupPipe;
   private final ActionPipe<ActivateSmartCardCommand> activateSmartCardPipe;
   private final ActionPipe<GetSmartCardStatusCommand> getSmartCardStatusCommandActionPipe;

   private final ActionPipe<ReAssignCardCommand> reAssignCardPipe;

   private final ActionPipe<WizardCompleteCommand> completePipe;
   private final ActionPipe<ProvisioningModeCommand> provisioningStatePipe;
   private final ActionPipe<PinOptionalCommand> pinOptionalActionPipe;
   private final ActionPipe<FetchTermsAndConditionsCommand> termsAndConditionsPipe;

   public WizardInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      createAndConnectPipe = sessionActionPipeCreator.createPipe(CreateAndConnectToCardCommand.class, Schedulers.io());
      setupUserDataPipe = sessionActionPipeCreator.createPipe(SetupUserDataCommand.class, Schedulers.io());
      activateSmartCardPipe = sessionActionPipeCreator.createPipe(ActivateSmartCardCommand.class, Schedulers.io());

      pinSetupFinishedPipe = sessionActionPipeCreator.createPipe(PinSetupFinishedEvent.class, Schedulers.io());
      startPinSetupPipe = sessionActionPipeCreator.createPipe(StartPinSetupAction.class, Schedulers.io());
      cancelPinSetupPipe = sessionActionPipeCreator.createPipe(CancelPinSetupAction.class, Schedulers.io());

      reAssignCardPipe = sessionActionPipeCreator.createPipe(ReAssignCardCommand.class, Schedulers.io());
      getSmartCardStatusCommandActionPipe = sessionActionPipeCreator.createPipe(GetSmartCardStatusCommand.class, Schedulers
            .io());

      completePipe = sessionActionPipeCreator.createPipe(WizardCompleteCommand.class, Schedulers.io());
      provisioningStatePipe = sessionActionPipeCreator.createPipe(ProvisioningModeCommand.class, Schedulers.io());
      pinOptionalActionPipe = sessionActionPipeCreator.createPipe(PinOptionalCommand.class, Schedulers.io());
      termsAndConditionsPipe = sessionActionPipeCreator.createPipe(FetchTermsAndConditionsCommand.class, Schedulers.io());
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

   public ActionPipe<CancelPinSetupAction> cancelPinSetupPipe() {
      return cancelPinSetupPipe;
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

   public ActionPipe<FetchTermsAndConditionsCommand> getTermsAndConditionsPipe() {
      return termsAndConditionsPipe;
   }
}
