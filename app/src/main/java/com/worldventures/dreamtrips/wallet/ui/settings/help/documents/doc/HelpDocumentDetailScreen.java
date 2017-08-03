package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc;


import com.worldventures.dreamtrips.wallet.ui.settings.common.model.WalletDocument;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

public interface HelpDocumentDetailScreen extends WalletScreen {

   WalletDocument getDocument();

   void showDocument(WalletDocument document);
}
