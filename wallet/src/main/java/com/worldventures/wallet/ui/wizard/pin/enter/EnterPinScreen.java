package com.worldventures.wallet.ui.wizard.pin.enter;

import com.worldventures.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.wallet.ui.wizard.pin.Action;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface EnterPinScreen extends WalletScreen, EnterPinDelegate.PinView {
   <T> OperationView<T> operationView();

   Action getPinAction();
}