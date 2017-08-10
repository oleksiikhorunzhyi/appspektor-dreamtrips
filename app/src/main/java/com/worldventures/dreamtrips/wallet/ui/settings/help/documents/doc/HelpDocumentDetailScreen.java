package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc;


import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.model.WalletDocumentModel;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

public interface HelpDocumentDetailScreen extends WalletScreen {

   WalletDocumentModel getDocument();

   void showDocument(WalletDocumentModel document);
}
