package com.worldventures.wallet.ui.settings.general.newcard.poweron;

import com.worldventures.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetView;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface NewCardPowerOnScreen extends WalletScreen, FactoryResetView {

   void setTitleWithSmartCardID(String scID);

   void showConfirmationUnassignOnBackend(String scId);

   OperationView<WipeSmartCardDataCommand> provideWipeOperationView();

}
