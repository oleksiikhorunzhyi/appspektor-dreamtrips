package com.worldventures.dreamtrips.wallet.ui.settings.security;

import android.content.Context;
import android.view.View;

import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import java.util.List;

import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public interface WalletSecuritySettingsScreen extends WalletScreen {

   void setAddRemovePinState(boolean isEnabled);

   void stealthModeStatus(boolean isEnabled);

   void lockStatus(boolean lock);

   void setLockToggleEnable(boolean enable);

   boolean isLockToggleChecked();

   void disableDefaultPaymentValue(long minutes);

   void autoClearSmartCardValue(long minutes);

   Observable<Boolean> lockStatus();

   Observable<Boolean> stealthModeStatus();

   void showSCNonConnectionDialog();

   List<View> getToggleableItems();

   Context getViewContext();

   OperationView<SetLockStateCommand> provideOperationSeLockState();

   OperationView<SetStealthModeCommand> provideOperationSetStealthMode();
}
