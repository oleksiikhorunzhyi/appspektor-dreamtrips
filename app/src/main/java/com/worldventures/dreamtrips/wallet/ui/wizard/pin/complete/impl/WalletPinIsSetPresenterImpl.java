package com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete.impl;


import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.PinWasSetAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete.WalletPinIsSetPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete.WalletPinIsSetScreen;

import io.techery.janet.helper.ActionStateSubscriber;

public class WalletPinIsSetPresenterImpl extends WalletPresenterImpl<WalletPinIsSetScreen> implements WalletPinIsSetPresenter {

   private final AnalyticsInteractor analyticsInteractor;
   private final WizardInteractor wizardInteractor;

   public WalletPinIsSetPresenterImpl(NavigatorConductor navigator, SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         AnalyticsInteractor analyticsInteractor, WizardInteractor wizardInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.analyticsInteractor = analyticsInteractor;
      this.wizardInteractor = wizardInteractor;
   }

   @Override
   public void attachView(WalletPinIsSetScreen view) {
      super.attachView(view);
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new PinWasSetAction()));
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void navigateToNextScreen() {
      wizardInteractor.provisioningStatePipe()
            .createObservable(ProvisioningModeCommand.fetchState())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<ProvisioningModeCommand>()
                  .onSuccess(command -> getNavigator().goWizardAssignUser(command.getResult())));
   }
}
