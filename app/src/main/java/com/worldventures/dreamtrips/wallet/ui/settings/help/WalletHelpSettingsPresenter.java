package com.worldventures.dreamtrips.wallet.ui.settings.help;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.WalletHelpDocumentsPath;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.SendFeedbackPath;
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

   void openSendFeedbackSection() {
      navigator.go(new SendFeedbackPath());
   }

   void openVideoSection() {
      navigator.go(new WalletHelpVideoPath());
   }

   public interface Screen extends WalletScreen {

   }

   public void openDocuments() {
      navigator.go(new WalletHelpDocumentsPath());
   }
}
