package com.worldventures.dreamtrips.wallet.ui.wizard.welcome.impl;


import android.text.TextUtils;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.WelcomeAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomeScreen;

public class WizardWelcomePresenterImpl extends WalletPresenterImpl<WizardWelcomeScreen> implements WizardWelcomePresenter {

   private final SessionHolder<UserSession> appSessionHolder;
   private final AnalyticsInteractor analyticsInteractor;
   private final WizardInteractor wizardInteractor;

   public WizardWelcomePresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         SessionHolder<UserSession> appSessionHolder, AnalyticsInteractor analyticsInteractor, WizardInteractor wizardInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.appSessionHolder = appSessionHolder;
      this.analyticsInteractor = analyticsInteractor;
      this.wizardInteractor = wizardInteractor;
   }

   @Override
   public void attachView(WizardWelcomeScreen view) {
      super.attachView(view);
      wizardInteractor.provisioningStatePipe().send(ProvisioningModeCommand.saveState(getView().getProvisionMode()));
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new WelcomeAction()));

      User user = appSessionHolder.get().get().getUser();
      view.userName(user.getFullName());
      loadUserPhoto(user.getAvatar().getThumb());
      view.welcomeMessage(user);
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
