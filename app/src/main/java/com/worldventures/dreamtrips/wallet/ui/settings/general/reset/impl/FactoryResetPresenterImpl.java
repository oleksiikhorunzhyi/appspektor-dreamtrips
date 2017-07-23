package com.worldventures.dreamtrips.wallet.ui.settings.general.reset.impl;


import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.FactoryResetInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetAction;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetScreen;

public class FactoryResetPresenterImpl extends WalletPresenterImpl<FactoryResetScreen> implements FactoryResetPresenter {

   private final FactoryResetDelegate factoryResetDelegate;

   public FactoryResetPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor, FactoryResetInteractor factoryResetInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.factoryResetDelegate = FactoryResetDelegate.create(factoryResetInteractor, analyticsInteractor,
            navigator, FactoryResetAction.GENERAL);
   }

   @Override
   public void attachView(FactoryResetScreen view) {
      super.attachView(view);
      factoryResetDelegate.bindView(getView());
   }

   @Override
   public void detachView(boolean retainInstance) {
      factoryResetDelegate.cancelFactoryReset();
      super.detachView(retainInstance);
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

}
