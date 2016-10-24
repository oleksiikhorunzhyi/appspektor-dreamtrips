package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.DisassociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchAndStoreDefaultAddressInfoCommand;
import com.worldventures.dreamtrips.wallet.service.command.wizard.WizardCheckCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.ReadActionPipe;
import io.techery.janet.smartcard.action.settings.StartPinSetupAction;
import io.techery.janet.smartcard.event.PinSetupFinishedEvent;
import rx.schedulers.Schedulers;

public final class WizardInteractor {
   private final ActionPipe<AssociateCardUserCommand> associateCardUserCommandPipe;
   private final ActionPipe<DisassociateCardUserCommand> disassociateCardUserCommandPipe;
   private final ActionPipe<CreateAndConnectToCardCommand> createAndConnectPipe;
   private final ActionPipe<SetupUserDataCommand> setupUserDataPipe;

   private final ReadActionPipe<PinSetupFinishedEvent> pinSetupFinishedPipe;
   private final ActionPipe<StartPinSetupAction> startPinSetupPipe;
   private final ActionPipe<ActivateSmartCardCommand> activateSmartCardPipe;
   private final ActionPipe<WizardCheckCommand> checksPipe;

   private final ActionPipe<FetchAndStoreDefaultAddressInfoCommand> fetchAndStoreDefaultAddressInfoPipe;

   public WizardInteractor(Janet janet) {
      associateCardUserCommandPipe = janet.createPipe(AssociateCardUserCommand.class, Schedulers.io());
      disassociateCardUserCommandPipe = janet.createPipe(DisassociateCardUserCommand.class, Schedulers.io());
      createAndConnectPipe = janet.createPipe(CreateAndConnectToCardCommand.class, Schedulers.io());
      setupUserDataPipe = janet.createPipe(SetupUserDataCommand.class, Schedulers.io());
      activateSmartCardPipe = janet.createPipe(ActivateSmartCardCommand.class, Schedulers.io());

      pinSetupFinishedPipe = janet.createPipe(PinSetupFinishedEvent.class, Schedulers.io());
      startPinSetupPipe = janet.createPipe(StartPinSetupAction.class, Schedulers.io());
      checksPipe = janet.createPipe(WizardCheckCommand.class, Schedulers.io());

      fetchAndStoreDefaultAddressInfoPipe = janet.createPipe(FetchAndStoreDefaultAddressInfoCommand.class, Schedulers.io());

      connect();
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

   public ActionPipe<AssociateCardUserCommand> associateCardUserCommandPipe() {
      return associateCardUserCommandPipe;
   }

   public ActionPipe<DisassociateCardUserCommand> disassociatePipe() {
      return disassociateCardUserCommandPipe;
   }

   public ActionPipe<WizardCheckCommand> checksPipe() {
      return checksPipe;
   }

   public ActionPipe<FetchAndStoreDefaultAddressInfoCommand> fetchAndStoreDefaultAddressInfoPipe() {
      return fetchAndStoreDefaultAddressInfoPipe;
   }

   private void connect() {
      associateCardUserCommandPipe
            .observeSuccess()
            .subscribe(command -> createAndConnectActionPipe()
                  .send(new CreateAndConnectToCardCommand(command.getResult())));
   }

}
