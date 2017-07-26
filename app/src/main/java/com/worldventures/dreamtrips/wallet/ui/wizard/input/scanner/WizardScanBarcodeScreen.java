package com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner;

import android.view.View;

import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.helper.InputDelegateView;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public interface WizardScanBarcodeScreen extends WalletScreen, InputDelegateView, ZXingScannerView.ResultHandler {

   void onPostEnterAnimation();

   void onPreExitAnimation();

   void startCamera();

   void showRationaleForCamera();

   void showDeniedForCamera();

   View getContentView();
}