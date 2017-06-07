package com.worldventures.dreamtrips.wallet.ui.settings.help;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.WalletHelpDocumentsPath;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.SendFeedbackPath;
import com.worldventures.dreamtrips.wallet.ui.settings.help.support.WalletCustomerSupportSettingsPath;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.WalletHelpVideoPath;

import javax.inject.Inject;

public class WalletHelpSettingsPresenter extends WalletPresenter<WalletHelpSettingsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;

   WalletHelpSettingsPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   public void goBack() {
      navigator.goBack();
   }

   public void openPaymentFeedbackScreen() {
      getView().hideBottomFeedbackMenu();
      // TODO: 6/7/17 Add payment feedback screen
   }

   void openVideoScreen() {
      navigator.go(new WalletHelpVideoPath());
   }

   void openCustomerSupportScreen() {
      navigator.go(new WalletCustomerSupportSettingsPath());
   }

   void openDocumentsScreen() {
      navigator.go(new WalletHelpDocumentsPath());
   }

   void handleVariantFeedback() {
      getView().showBottomFeedbackMenu();
   }

   public void openOtherFeedbackScreen() {
      getView().hideBottomFeedbackMenu();
      navigator.go(new SendFeedbackPath(SendFeedbackPath.FeedbackType.SmartCardFeedback));
   }

   public interface Screen extends WalletScreen {

      void showBottomFeedbackMenu();

      void hideBottomFeedbackMenu();
   }
}
