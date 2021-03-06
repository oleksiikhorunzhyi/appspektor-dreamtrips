package com.worldventures.wallet.ui.settings.security.lostcard;

import com.worldventures.wallet.service.lostcard.command.UpdateTrackingStatusCommand;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public interface LostCardScreen extends WalletScreen {

   Observable<Boolean> observeTrackingEnable();

   void setMapEnabled(boolean enabled);

   void switcherEnable(boolean enable);

   void setTrackingSwitchStatus(boolean checked);

   void showRationaleForLocation();

   void showDeniedForLocation();

   void showDisableConfirmationDialog();

   OperationView<UpdateTrackingStatusCommand> provideOperationUpdateTrackingStatus();

   void revertTrackingSwitch();
}
