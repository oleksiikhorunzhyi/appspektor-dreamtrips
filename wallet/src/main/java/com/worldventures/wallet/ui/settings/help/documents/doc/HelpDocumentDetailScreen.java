package com.worldventures.wallet.ui.settings.help.documents.doc;


import com.worldventures.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.wallet.ui.settings.help.documents.model.WalletDocumentModel;

public interface HelpDocumentDetailScreen extends WalletScreen {

   WalletDocumentModel getDocument();

   void showDocument(WalletDocumentModel document);
}
