package com.worldventures.wallet.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.wallet.service.command.SetupUserDataCommand;
import com.worldventures.wallet.service.command.http.FetchSmartCardAgreementsCommand;
import com.worldventures.wallet.service.command.http.GetSmartCardStatusCommand;
import com.worldventures.wallet.service.command.wizard.AddDummyRecordCommand;
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

   private final ActionPipe<SetupUserDataCommand> setupUserDataPipe;

   private final ReadActionPipe<PinSetupFinishedEvent> pinSetupFinishedPipe;
   private final ActionPipe<AddDummyRecordCommand> addDummyPipe;
   private final ActionPipe<StartPinSetupAction> startPinSetupPipe;
   private final ActionPipe<CancelPinSetupAction> cancelPinSetupPipe;
   private final ActionPipe<ActivateSmartCardCommand> activateSmartCardPipe;
   private final ActionPipe<GetSmartCardStatusCommand> getSmartCardStatusCommandActionPipe;

   private final ActionPipe<ReAssignCardCommand> reAssignCardPipe;

   private final ActionPipe<WizardCompleteCommand> completePipe;
   private final ActionPipe<ProvisioningModeCommand> provisioningStatePipe;
   private final ActionPipe<PinOptionalCommand> pinOptionalActionPipe;
   private final ActionPipe<FetchSmartCardAgreementsCommand> fetchSmartCardAgreementsPipe;

   public WizardInteractor(SessionActionPipeCreator pipeCreator) {
      setupUserDataPipe = pipeCreator.createPipe(SetupUserDataCommand.class, Schedulers.io());
      addDummyPipe = pipeCreator.createPipe(AddDummyRecordCommand.class, Schedulers.io());
      activateSmartCardPipe = pipeCreator.createPipe(ActivateSmartCardCommand.class, Schedulers.io());

      pinSetupFinishedPipe = pipeCreator.createPipe(PinSetupFinishedEvent.class, Schedulers.io());
      startPinSetupPipe = pipeCreator.createPipe(StartPinSetupAction.class, Schedulers.io());
      cancelPinSetupPipe = pipeCreator.createPipe(CancelPinSetupAction.class, Schedulers.io());

      reAssignCardPipe = pipeCreator.createPipe(ReAssignCardCommand.class, Schedulers.io());
      getSmartCardStatusCommandActionPipe = pipeCreator.createPipe(GetSmartCardStatusCommand.class, Schedulers
            .io());

      completePipe = pipeCreator.createPipe(WizardCompleteCommand.class, Schedulers.io());
      provisioningStatePipe = pipeCreator.createPipe(ProvisioningModeCommand.class, Schedulers.io());
      pinOptionalActionPipe = pipeCreator.createPipe(PinOptionalCommand.class, Schedulers.io());
      fetchSmartCardAgreementsPipe = pipeCreator.createPipe(FetchSmartCardAgreementsCommand.class, Schedulers.io());
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

   public ActionPipe<WizardCompleteCommand> getCompletePipe() {
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

   public ActionPipe<FetchSmartCardAgreementsCommand> fetchSmartCardAgreementsPipe() {
      return fetchSmartCardAgreementsPipe;
   }

   public ActionPipe<AddDummyRecordCommand> getAddDummyPipe() {
      return addDummyPipe;
   }
}
