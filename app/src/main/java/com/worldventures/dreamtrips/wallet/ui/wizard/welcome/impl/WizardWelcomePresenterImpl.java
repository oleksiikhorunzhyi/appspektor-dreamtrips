package com.worldventures.dreamtrips.wallet.ui.wizard.welcome.impl;


import android.text.TextUtils;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.WelcomeAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.WalletSocialInfoProvider;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomeScreen;

public class WizardWelcomePresenterImpl extends WalletPresenterImpl<WizardWelcomeScreen> implements WizardWelcomePresenter {

   private final WalletSocialInfoProvider socialInfoProvider;
   private final AnalyticsInteractor analyticsInteractor;
   private final WizardInteractor wizardInteractor;

   public WizardWelcomePresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         WalletSocialInfoProvider socialInfoProvider, AnalyticsInteractor analyticsInteractor, WizardInteractor wizardInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.socialInfoProvider = socialInfoProvider;
      this.analyticsInteractor = analyticsInteractor;
      this.wizardInteractor = wizardInteractor;
   }

   @Override
   public void attachView(WizardWelcomeScreen view) {
      super.attachView(view);
      wizardInteractor.provisioningStatePipe().send(ProvisioningModeCommand.saveState(getView().getProvisionMode()));
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new WelcomeAction()));

      view.userName(socialInfoProvider.fullName());
      loadUserPhoto(socialInfoProvider.photoThumb());
      view.welcomeMessage(socialInfoProvider.memberStatus());
      view.showAnimation();
   }

   private void loadUserPhoto(String avatarPath) {
      if (!TextUtils.isEmpty(avatarPath)) getView().userPhoto(avatarPath);
   }

   @Override
   public void setupCardClicked() {
      getNavigator().goWizardPowerOn();
   }

   @Override
   public void backButtonClicked() {
      getNavigator().goBack();
   }
}
