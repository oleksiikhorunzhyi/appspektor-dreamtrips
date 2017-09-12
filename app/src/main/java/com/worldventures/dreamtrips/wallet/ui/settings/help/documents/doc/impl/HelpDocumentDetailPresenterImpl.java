package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc.impl;


import android.net.Uri;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc.HelpDocumentDetailPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc.HelpDocumentDetailScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.model.WalletDocumentModel;

public class HelpDocumentDetailPresenterImpl extends WalletPresenterImpl<HelpDocumentDetailScreen> implements HelpDocumentDetailPresenter {

   public HelpDocumentDetailPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate) {
      super(navigator, deviceConnectionDelegate);
   }

   @Override
   public void attachView(HelpDocumentDetailScreen view) {
      super.attachView(view);
      final WalletDocumentModel document = getView().getDocument();
      getView().showDocument(document);
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void sendEmail(Uri uri, String title) {
      getNavigator().goSendEmail(uri, title);
   }
}
