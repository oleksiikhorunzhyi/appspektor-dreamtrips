package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.poweron;

import com.worldventures.dreamtrips.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetView;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface NewCardPowerOnScreen extends WalletScreen, FactoryResetView {

   void setTitleWithSmartCardID(String scID);

   void showConfirmationUnassignOnBackend(String scId);

   OperationView<WipeSmartCardDataCommand> provideWipeOperationView();

}
