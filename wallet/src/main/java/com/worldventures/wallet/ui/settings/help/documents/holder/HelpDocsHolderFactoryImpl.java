package com.worldventures.wallet.ui.settings.help.documents.holder;

import android.view.ViewGroup;

import com.worldventures.wallet.R;
import com.worldventures.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.wallet.ui.settings.help.documents.model.WalletDocumentModel;
import com.worldventures.wallet.ui.settings.help.documents.model.WalletLoadMoreModel;

import static android.databinding.DataBindingUtil.bind;
import static android.view.LayoutInflater.from;

public class HelpDocsHolderFactoryImpl implements HelpDocsTypeFactory {

   private DocumentHolder.Callback documentCallback;

   public HelpDocsHolderFactoryImpl(DocumentHolder.Callback documentCallback) {
      this.documentCallback = documentCallback;
   }

   @Override
   public BaseHolder holder(ViewGroup parent, int viewType) {
      if (viewType == R.layout.item_wallet_document) {
         return new DocumentHolder(bind(from(parent.getContext()).inflate(viewType, parent, false)), documentCallback);
      } else if (viewType == R.layout.item_wallet_load_more) {
         return new LoadMoreHolder(bind(from(parent.getContext()).inflate(viewType, parent, false)));
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public int type(WalletDocumentModel document) {
      return R.layout.item_wallet_document;
   }

   @Override
   public int type(WalletLoadMoreModel walletLoadMore) {
      return R.layout.item_wallet_load_more;
   }
}
