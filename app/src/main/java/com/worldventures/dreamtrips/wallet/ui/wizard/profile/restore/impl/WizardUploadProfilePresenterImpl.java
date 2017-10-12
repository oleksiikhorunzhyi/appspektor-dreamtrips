package com.worldventures.dreamtrips.wallet.ui.wizard.profile.restore.impl;


import com.worldventures.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.PhotoWasSetAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.restore.WizardUploadProfilePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.restore.WizardUploadProfileScreen;

import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class WizardUploadProfilePresenterImpl extends WalletPresenterImpl<WizardUploadProfileScreen> implements WizardUploadProfilePresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final WizardInteractor wizardInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;

   public WizardUploadProfilePresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate, SmartCardInteractor smartCardInteractor,
         WizardInteractor wizardInteractor, WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.wizardInteractor = wizardInteractor;
      this.analyticsInteractor = analyticsInteractor;
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
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationSetupUserData())
                  .onSuccess(command -> onUserSetupSuccess(command.getResult()))
                  .onFail((command, throwable) -> {
                     getView().showRetryDialog();
                     Timber.e(throwable, "");
                  })
                  .create());
   }

   private void fetchSmartCardUserData() {
      smartCardInteractor.smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.fetch())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
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
      analyticsInteractor.walletAnalyticsPipe()
            .send(new WalletAnalyticsCommand(
                  user.userPhoto() != null ? PhotoWasSetAction.methodDefault() : PhotoWasSetAction.noPhoto())
            );
      getNavigator().goWizardAssignUser(getView().getProvisionMode());
   }
}
