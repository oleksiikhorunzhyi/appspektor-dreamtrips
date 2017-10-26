package com.worldventures.wallet.ui.wizard.input.manual;

import android.support.annotation.NonNull;

import com.worldventures.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.wallet.ui.wizard.input.helper.InputDelegateView;

import rx.Observable;

public interface WizardManualInputScreen extends WalletScreen, InputDelegateView {

   void buttonEnable(boolean isEnable);

   @NonNull
   Observable<CharSequence> scidInput();

   int getScIdLength();
}