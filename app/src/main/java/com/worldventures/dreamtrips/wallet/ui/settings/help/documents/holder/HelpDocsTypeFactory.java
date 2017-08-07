package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.holder;

import com.worldventures.dreamtrips.wallet.ui.settings.common.model.WalletDocument;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.WalletLoadMore;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.HolderTypeFactory;

public interface HelpDocsTypeFactory extends HolderTypeFactory {

   int type(WalletDocument document);

   int type(WalletLoadMore walletLoadMore);
}
