package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc.impl;


import android.net.Uri;

import com.worldventures.dreamtrips.modules.infopages.model.Document;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc.HelpDocumentDetailPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc.HelpDocumentDetailScreen;

public class HelpDocumentDetailPresenterImpl extends WalletPresenterImpl<HelpDocumentDetailScreen> implements HelpDocumentDetailPresenter {

   public HelpDocumentDetailPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService) {
      super(navigator, smartCardInteractor, networkService);
   }

   @Override
   public void attachView(HelpDocumentDetailScreen view) {
      super.attachView(view);
      final Document document = getView().getDocument();
      getView().showDocument(document);
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void sendEmail(Uri uri, String title) {
      getNavigator().goSendEmail(getView().getViewContext(), uri, title);
   }
}
