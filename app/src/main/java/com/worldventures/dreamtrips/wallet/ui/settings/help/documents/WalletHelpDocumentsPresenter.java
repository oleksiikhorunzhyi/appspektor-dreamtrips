package com.worldventures.dreamtrips.wallet.ui.settings.help.documents;


import com.worldventures.dreamtrips.modules.infopages.model.Document;
import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface WalletHelpDocumentsPresenter extends WalletPresenter<WalletHelpDocumentsScreen> {

   void loadNextDocuments();

   void goBack();

   HttpErrorHandlingUtil httpErrorHandlingUtil();

   void refreshDocuments();

   void openDocument(Document document);
}
