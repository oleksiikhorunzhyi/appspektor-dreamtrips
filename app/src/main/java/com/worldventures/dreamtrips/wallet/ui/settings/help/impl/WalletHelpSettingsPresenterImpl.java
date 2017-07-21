package com.worldventures.dreamtrips.wallet.ui.settings.help.impl;


import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.settings.help.WalletHelpSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.WalletHelpSettingsScreen;

public class WalletHelpSettingsPresenterImpl extends WalletPresenterImpl<WalletHelpSettingsScreen> implements WalletHelpSettingsPresenter {

   public WalletHelpSettingsPresenterImpl(NavigatorConductor navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService) {
      super(navigator, smartCardInteractor, networkService);
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
