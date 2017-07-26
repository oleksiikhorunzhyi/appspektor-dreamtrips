package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc;


import android.content.Context;

import com.worldventures.dreamtrips.modules.infopages.model.Document;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

public interface HelpDocumentDetailScreen extends WalletScreen {

   Document getDocument();

   void showDocument(Document document);

   Context getViewContext();
}
