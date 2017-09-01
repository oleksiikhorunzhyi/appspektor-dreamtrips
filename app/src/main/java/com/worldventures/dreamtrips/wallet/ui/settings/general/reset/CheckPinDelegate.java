package com.worldventures.dreamtrips.wallet.ui.settings.general.reset;


import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.FactoryResetInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import io.techery.janet.smartcard.action.settings.CheckPinStatusAction;
import io.techery.janet.smartcard.event.PinStatusEvent;
import rx.android.schedulers.AndroidSchedulers;

public class CheckPinDelegate {
   private final SmartCardInteractor smartCardInteractor;
   private final FactoryResetInteractor factoryResetInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;
   private final Navigator navigator;
   private final FactoryResetAction factoryResetAction;
   private FactoryResetDelegate factoryResetDelegate;

   public CheckPinDelegate(SmartCardInteractor smartCardInteractor, FactoryResetInteractor factoryResetInteractor,
         WalletAnalyticsInteractor analyticsInteractor, Navigator navigator, FactoryResetAction action) {
      this.smartCardInteractor = smartCardInteractor;
      this.factoryResetInteractor = factoryResetInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.navigator = navigator;
      this.factoryResetAction = action;
   }

   public void observePinStatus(FactoryResetView view) {
      smartCardInteractor.pinStatusEventPipe()
            .observeSuccessWithReplay()
            .compose(RxLifecycleAndroid.bindView(view.getView()))
            .observeOn(AndroidSchedulers.mainThread())
            .map(pinStatusEvent -> pinStatusEvent.pinStatus != PinStatusEvent.PinStatus.DISABLED)
            .startWith(true)
            .subscribe(this::createFactoryResetDelegate);
      smartCardInteractor.checkPinStatusActionPipe().send(new CheckPinStatusAction());
   }

   public void createFactoryResetDelegate(boolean isEnabled) {
      factoryResetDelegate = FactoryResetDelegate.create(factoryResetInteractor, analyticsInteractor, navigator,
            factoryResetAction, isEnabled ? PinMode.ENABLED : PinMode.DISABLED);
   }

   public FactoryResetDelegate getFactoryResetDelegate() {
      return factoryResetDelegate;
   }
}
