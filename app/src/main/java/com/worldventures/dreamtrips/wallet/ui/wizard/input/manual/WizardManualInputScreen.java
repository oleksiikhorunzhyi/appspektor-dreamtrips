package com.worldventures.dreamtrips.wallet.ui.wizard.input.manual;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.helper.InputDelegateView;

import rx.Observable;

public interface WizardManualInputScreen extends WalletScreen, InputDelegateView {

   void buttonEnable(boolean isEnable);

   @NonNull
   Observable<CharSequence> scidInput();

   int getScIdLength();
}