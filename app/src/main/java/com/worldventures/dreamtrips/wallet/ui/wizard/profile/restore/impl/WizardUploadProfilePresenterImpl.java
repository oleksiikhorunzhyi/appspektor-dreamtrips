package com.worldventures.dreamtrips.wallet.ui.wizard.profile.restore.impl;


import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.PhotoWasSetAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.restore.WizardUploadProfilePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.restore.WizardUploadProfileScreen;
import com.worldventures.dreamtrips.wallet.util.WalletFeatureHelper;

import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import timber.log.Timber;

public class WizardUploadProfilePresenterImpl extends WalletPresenterImpl<WizardUploadProfileScreen> implements WizardUploadProfilePresenter {

   private final WizardInteractor wizardInteractor;
   private final AnalyticsInteractor analyticsInteractor;
   private final WalletFeatureHelper featureHelper;

   public WizardUploadProfilePresenterImpl(NavigatorConductor navigator, SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         WizardInteractor wizardInteractor, AnalyticsInteractor analyticsInteractor, WalletFeatureHelper featureHelper) {
      super(navigator, smartCardInteractor, networkService);
      this.wizardInteractor = wizardInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.featureHelper = featureHelper;
   }

   @Override
   public void attachView(WizardUploadProfileScreen view) {
      super.attachView(view);
      observeSetupUserSmartCardData();
      fetchSmartCardUserData();
   }

   private void observeSetupUserSmartCardData() {
      wizardInteractor.setupUserDataPipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.setupUserDataPipe()))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationSetupUserData())
                  .onSuccess(command -> onUserSetupSuccess(command.getResult()))
                  .onFail((command, throwable) -> {
                     getView().showRetryDialog();
                     Timber.e(throwable, "");
                  })
                  .create());
   }

   private void fetchSmartCardUserData() {
      getSmartCardInteractor().smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .map(Command::getResult)
            .subscribe(this::handleSmartCardUserExisting);
   }

   @Override
   public void retryUpload() {
      fetchSmartCardUserData();
   }

   private void handleSmartCardUserExisting(SmartCardUser smartCardUser) {
      wizardInteractor.setupUserDataPipe().send(new SetupUserDataCommand(smartCardUser));
   }

   private void onUserSetupSuccess(SmartCardUser user) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(
                  user.userPhoto() != null ? PhotoWasSetAction.methodDefault() : PhotoWasSetAction.noPhoto())
            );
      featureHelper.navigateFromSetupUserScreen(getNavigator());
   }
}
