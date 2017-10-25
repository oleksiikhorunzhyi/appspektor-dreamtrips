package com.worldventures.dreamtrips.wallet.ui.wizard.pin.success.impl;


import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.success.PinSetSuccessPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.success.PinSetSuccessScreen;

public class PinSetSuccessPresenterImpl extends WalletPresenterImpl<PinSetSuccessScreen> implements PinSetSuccessPresenter {

   public PinSetSuccessPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate) {
      super(navigator, deviceConnectionDelegate);
   }

   @Override
   public void attachView(PinSetSuccessScreen view) {
      super.attachView(view);
      final Action pinAction = getView().getPinAction();
      view.showMode(pinAction);
   }

   @Override
   public void goToBack() {
      getNavigator().goBack();
   }

   @Override
   public void goToNext() {
      getNavigator().goBack();
   }
}
