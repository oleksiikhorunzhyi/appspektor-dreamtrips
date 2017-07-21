package com.worldventures.dreamtrips.wallet.ui.wizard.pin.success.impl;


import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.success.PinSetSuccessPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.success.PinSetSuccessScreen;

public class PinSetSuccessPresenterImpl extends WalletPresenterImpl<PinSetSuccessScreen> implements PinSetSuccessPresenter {

   public PinSetSuccessPresenterImpl(NavigatorConductor navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService) {
      super(navigator, smartCardInteractor, networkService);
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
