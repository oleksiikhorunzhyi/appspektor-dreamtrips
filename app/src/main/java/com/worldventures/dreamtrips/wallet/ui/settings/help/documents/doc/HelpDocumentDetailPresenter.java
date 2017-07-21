package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc;


import android.net.Uri;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface HelpDocumentDetailPresenter extends WalletPresenterI<HelpDocumentDetailScreen> {

   void goBack();

   void sendEmail(Uri uri, String title);
}
