package com.worldventures.dreamtrips.wallet.ui.settings.help.support.impl;


import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.service.command.settings.WalletSettingsInteractor;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.GetCustomerSupportContactCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.help.support.WalletCustomerSupportSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.support.WalletCustomerSupportSettingsScreen;
import com.worldventures.dreamtrips.wallet.util.GuaranteedProgressVisibilityTransformer;
import com.worldventures.dreamtrips.wallet.util.NoProgressAfterSuccessTransformer;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class WalletCustomerSupportSettingsPresenterImpl extends WalletPresenterImpl<WalletCustomerSupportSettingsScreen> implements WalletCustomerSupportSettingsPresenter {

   private final WalletSettingsInteractor walletSettingsInteractor;
   private final HttpErrorHandlingUtil httpErrorHandlingUtil;

   public WalletCustomerSupportSettingsPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletSettingsInteractor walletSettingsInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      super(navigator, deviceConnectionDelegate);
      this.walletSettingsInteractor = walletSettingsInteractor;
      this.httpErrorHandlingUtil = httpErrorHandlingUtil;
   }

   @Override
   public void attachView(WalletCustomerSupportSettingsScreen view) {
      super.attachView(view);
      observeContact();
      fetchCustomerSupportContact();
   }

   private void observeContact() {
      walletSettingsInteractor.customerSupportContactPipe()
            .observeWithReplay()
            .compose(new NoProgressAfterSuccessTransformer<>())
            .compose(new GuaranteedProgressVisibilityTransformer<>())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onSuccess(command -> getView().bindData(command.getResult()))
                  .create());
   }

   @Override
   public void fetchCustomerSupportContact() {
      walletSettingsInteractor.customerSupportContactPipe().send(new GetCustomerSupportContactCommand());
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void dialPhoneNumber(String phoneNumber) {
      getNavigator().goDialer(phoneNumber);
   }

   @Override
   public void openCustomerSupportFeedbackScreen() {
      getNavigator().goSendCustomerSupportFeedback();
   }

   @Override
   public HttpErrorHandlingUtil httpErrorHandlingUtil() {
      return httpErrorHandlingUtil;
   }

}