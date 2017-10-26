package com.worldventures.wallet.ui.settings.help.documents.doc;


import android.net.Uri;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface HelpDocumentDetailPresenter extends WalletPresenter<HelpDocumentDetailScreen> {

   void goBack();

   void sendEmail(Uri uri, String title);
}
