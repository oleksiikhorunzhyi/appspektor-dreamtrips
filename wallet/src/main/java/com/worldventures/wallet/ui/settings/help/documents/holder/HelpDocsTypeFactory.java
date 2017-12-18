package com.worldventures.wallet.ui.settings.help.documents.holder;

import com.worldventures.wallet.ui.common.adapter.HolderTypeFactory;
import com.worldventures.wallet.ui.settings.help.documents.model.WalletDocumentModel;
import com.worldventures.wallet.ui.settings.help.documents.model.WalletLoadMoreModel;

public interface HelpDocsTypeFactory extends HolderTypeFactory {

   int type(WalletDocumentModel document);

   int type(WalletLoadMoreModel walletLoadMore);
}
