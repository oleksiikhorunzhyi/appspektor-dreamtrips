package com.worldventures.wallet.ui.common.base;


import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;

public class WalletDeviceConnectionDelegateImpl implements WalletDeviceConnectionDelegate {

   private final SmartCardInteractor smartCardInteractor;

   public WalletDeviceConnectionDelegateImpl(SmartCardInteractor smartCardInteractor) {
      this.smartCardInteractor = smartCardInteractor;
   }

   @Override
   public void setup(WalletScreen view) {
      smartCardInteractor.deviceStatePipe()
            .observeSuccessWithReplay()
            .throttleLast(1, TimeUnit.SECONDS)
            .map(command -> command.getResult().getConnectionStatus())
            .distinctUntilChanged()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(view::showConnectionStatus);
      smartCardInteractor.deviceStatePipe().send(DeviceStateCommand.Companion.fetch());
   }
}
