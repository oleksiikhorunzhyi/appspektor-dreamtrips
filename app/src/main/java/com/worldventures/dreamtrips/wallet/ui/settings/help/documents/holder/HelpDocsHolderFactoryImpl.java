package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.holder;

import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.model.WalletDocumentModel;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.model.WalletLoadMoreModel;

import static android.databinding.DataBindingUtil.bind;
import static android.view.LayoutInflater.from;

public class HelpDocsHolderFactoryImpl implements HelpDocsTypeFactory {

   private final DocumentHolder.Callback documentCallback;

   public HelpDocsHolderFactoryImpl(DocumentHolder.Callback documentCallback) {
      this.documentCallback = documentCallback;
   }

   @Override
   public BaseHolder holder(ViewGroup parent, int viewType) {
      switch (viewType) {
         case R.layout.adapter_item_document:
            return new DocumentHolder(bind(from(parent.getContext()).inflate(viewType, parent, false)), documentCallback);
         case R.layout.adapter_item_load_more_feed:
            return new LoadMoreHolder(bind(from(parent.getContext()).inflate(viewType, parent, false)));
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public int type(WalletDocumentModel document) {
      return R.layout.adapter_item_document;
   }

   @Override
   public int type(WalletLoadMoreModel walletLoadMore) {
      return R.layout.adapter_item_load_more_feed;
   }
}
