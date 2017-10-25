package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.detection;

import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetView;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface ExistingCardDetectScreen extends WalletScreen, FactoryResetView {

   OperationView<ActiveSmartCardCommand> provideActiveSmartCardOperationView();

   OperationView<DeviceStateCommand> provideDeviceStateOperationView();

   OperationView<WipeSmartCardDataCommand> provideWipeOperationView();

   void setSmartCardId(String scId);

   void modeConnectedSmartCard();

   void modeDisconnectedSmartCard();

   void showConfirmationUnassignDialog(String scId);

   void showConfirmationUnassignOnBackend(String scId);

}