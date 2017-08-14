package com.worldventures.dreamtrips.wallet.ui.settings.help.documents;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.model.WalletDocumentModel;

public interface WalletHelpDocumentsPresenter extends WalletPresenter<WalletHelpDocumentsScreen> {

   void loadNextDocuments();

   void goBack();

   void refreshDocuments();

   void openDocument(WalletDocumentModel document);
}
