package com.worldventures.wallet.ui.wizard.assign.impl;


import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.wallet.service.RecordInteractor;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.WizardInteractor;
import com.worldventures.wallet.service.command.wizard.WizardCompleteCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.wizard.assign.WizardAssignDelegate;
import com.worldventures.wallet.ui.wizard.assign.WizardAssignUserPresenter;
import com.worldventures.wallet.ui.wizard.assign.WizardAssignUserScreen;
import com.worldventures.wallet.util.WalletFeatureHelper;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class WizardAssignUserPresenterImpl extends WalletPresenterImpl<WizardAssignUserScreen> implements WizardAssignUserPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final WizardInteractor wizardInteractor;
   private final RecordInteractor recordInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;
   private final HttpErrorHandlingUtil httpErrorHandlingUtil;
   private final WalletFeatureHelper walletFeatureHelper;

   private WizardAssignDelegate wizardAssignDelegate;

   public WizardAssignUserPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, WizardInteractor wizardInteractor, RecordInteractor recordInteractor,
         WalletAnalyticsInteractor analyticsInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil,
         WalletFeatureHelper walletFeatureHelper) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.wizardInteractor = wizardInteractor;
      this.recordInteractor = recordInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.httpErrorHandlingUtil = httpErrorHandlingUtil;
      this.walletFeatureHelper = walletFeatureHelper;
   }

   @Override
   public void attachView(WizardAssignUserScreen view) {
      super.attachView(view);
      this.wizardAssignDelegate = WizardAssignDelegate.create(getView().getProvisionMode(), wizardInteractor,
            recordInteractor, analyticsInteractor, smartCardInteractor, walletFeatureHelper, getNavigator());
      observeComplete();
      onWizardComplete();
   }

   @Override
   public void onWizardComplete() {
      wizardInteractor.completePipe().send(new WizardCompleteCommand());
   }

   @Override
   public void onWizardCancel() {
      getNavigator().goBack();
   }

   private void observeComplete() {
      wizardInteractor.completePipe()
            .observe()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onSuccess(command -> wizardAssignDelegate.onAssignUserSuccess(getView()))
                  .create());
   }

   @Override
   public HttpErrorHandlingUtil httpErrorHandlingUtil() {
      return httpErrorHandlingUtil;
   }
}
