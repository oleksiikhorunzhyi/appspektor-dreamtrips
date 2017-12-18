package com.worldventures.wallet.ui.settings.help.impl;


import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.help.WalletHelpSettingsPresenter;
import com.worldventures.wallet.ui.settings.help.WalletHelpSettingsScreen;

public class WalletHelpSettingsPresenterImpl extends WalletPresenterImpl<WalletHelpSettingsScreen> implements WalletHelpSettingsPresenter {

   public WalletHelpSettingsPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate) {
      super(navigator, deviceConnectionDelegate);
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void openPaymentFeedbackScreen() {
      getView().hideBottomFeedbackMenu();
      getNavigator().goPaymentFeedBack();
   }

   @Override
   public void openVideoScreen() {
      getNavigator().goWalletHelpVideo();
   }

   @Override
   public void openCustomerSupportScreen() {
      getNavigator().goWalletCustomerSupport();
   }

   @Override
   public void openDocumentsScreen() {
      getNavigator().goWalletHelpDocuments();
   }

   @Override
   public void handleVariantFeedback() {
      getView().showBottomFeedbackMenu();
   }

   @Override
   public void openOtherFeedbackScreen() {
      getView().hideBottomFeedbackMenu();
      getNavigator().goSendSmartCardFeedback();
   }
}
