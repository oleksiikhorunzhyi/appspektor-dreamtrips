package com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner;

import android.view.View;

import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.helper.InputDelegateView;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WizardScanBarcodeScreen extends WalletScreen, InputDelegateView {

   void onPostEnterAnimation();

   void onPreExitAnimation();

   void startCamera();

   void showRationaleForCamera();

   void showDeniedForCamera();

   View getContentView();

   void reset();

   OperationView<SmartCardUserCommand> provideOperationFetchSmartCardUser();
}