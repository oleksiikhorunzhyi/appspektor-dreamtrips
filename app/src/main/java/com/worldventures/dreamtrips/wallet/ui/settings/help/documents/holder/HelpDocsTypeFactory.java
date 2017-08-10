package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.holder;

import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.model.WalletDocumentModel;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.model.WalletLoadMoreModel;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.HolderTypeFactory;

public interface HelpDocsTypeFactory extends HolderTypeFactory {

   int type(WalletDocumentModel document);

   int type(WalletLoadMoreModel walletLoadMore);
}
