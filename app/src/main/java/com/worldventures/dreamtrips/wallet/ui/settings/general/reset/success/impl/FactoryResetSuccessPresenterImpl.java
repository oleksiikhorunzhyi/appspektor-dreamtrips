package com.worldventures.dreamtrips.wallet.ui.settings.general.reset.success.impl;


import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.settings.FactoryResetAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.success.FactoryResetSuccessPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.success.FactoryResetSuccessScreen;

public class FactoryResetSuccessPresenterImpl extends WalletPresenterImpl<FactoryResetSuccessScreen> implements FactoryResetSuccessPresenter {

   private final AnalyticsInteractor analyticsInteractor;

   public FactoryResetSuccessPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(FactoryResetSuccessScreen view) {
      super.attachView(view);
      trackScreen();
   }

   private void trackScreen() {
      final WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(new FactoryResetAction());
      analyticsInteractor.walletAnalyticsCommandPipe().send(analyticsCommand);
   }

   @Override
   public void navigateNext() {
      getNavigator().goBack();
   }
}
