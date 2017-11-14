package com.worldventures.wallet.ui.settings.security.offline_mode;

import com.worldventures.wallet.service.command.offline_mode.SwitchOfflineModeCommand;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public interface WalletOfflineModeSettingsScreen extends WalletScreen {

   Observable<Boolean> observeOfflineModeSwitcher();

   OperationView<SwitchOfflineModeCommand> provideOperationView();

   void showConfirmationDialog(boolean enable);

   void setOfflineModeState(boolean enabled);

}