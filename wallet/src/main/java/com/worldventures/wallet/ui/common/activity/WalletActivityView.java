package com.worldventures.wallet.ui.common.activity;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.worldventures.wallet.ui.common.base.screen.RxLifecycleView;

public interface WalletActivityView extends MvpView, RxLifecycleView {

   void openBluetoothSettings();
}
