package com.worldventures.wallet.ui.wizard.welcome.impl;


import android.text.TextUtils;

import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.wizard.WelcomeAction;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.WalletSocialInfoProvider;
import com.worldventures.wallet.service.WizardInteractor;
import com.worldventures.wallet.service.provisioning.ProvisioningModeCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.wizard.welcome.WizardWelcomePresenter;
import com.worldventures.wallet.ui.wizard.welcome.WizardWelcomeScreen;

public class WizardWelcomePresenterImpl extends WalletPresenterImpl<WizardWelcomeScreen> implements WizardWelcomePresenter {

   private final WalletSocialInfoProvider socialInfoProvider;
   private final WalletAnalyticsInteractor analyticsInteractor;
   private final WizardInteractor wizardInteractor;

   public WizardWelcomePresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletSocialInfoProvider socialInfoProvider, WalletAnalyticsInteractor analyticsInteractor, WizardInteractor wizardInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.socialInfoProvider = socialInfoProvider;
      this.analyticsInteractor = analyticsInteractor;
      this.wizardInteractor = wizardInteractor;
   }

   @Override
   public void attachView(WizardWelcomeScreen view) {
      super.attachView(view);
      wizardInteractor.provisioningStatePipe().send(ProvisioningModeCommand.saveState(getView().getProvisionMode()));
      analyticsInteractor.walletAnalyticsPipe().send(new WalletAnalyticsCommand(new WelcomeAction()));

      view.userName(socialInfoProvider.fullName());
      loadUserPhoto(socialInfoProvider.photoThumb());
      view.welcomeMessage(socialInfoProvider.memberStatus());
      view.showAnimation();
   }

   private void loadUserPhoto(String avatarPath) {
      if (!TextUtils.isEmpty(avatarPath)) {
         getView().userPhoto(avatarPath);
      }
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
