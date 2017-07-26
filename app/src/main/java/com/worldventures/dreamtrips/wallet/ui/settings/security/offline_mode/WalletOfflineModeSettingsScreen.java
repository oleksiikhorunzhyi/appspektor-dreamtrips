package com.worldventures.dreamtrips.wallet.ui.settings.security.offline_mode;

import android.content.Context;

import com.worldventures.dreamtrips.wallet.service.command.offline_mode.SwitchOfflineModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public interface WalletOfflineModeSettingsScreen extends WalletScreen {

   Observable<Boolean> observeOfflineModeSwitcher();

   OperationView<SwitchOfflineModeCommand> provideOperationView();

   void showConfirmationDialog(boolean enable);

   void setOfflineModeState(boolean enabled);

   Context getViewContext();
}