package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.holder;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.databinding.AdapterItemDocumentBinding;
import com.worldventures.dreamtrips.databinding.AdapterItemLoadMoreFeedBinding;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.WalletDocument;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.WalletLoadMore;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;

public class HelpDocsHolderFactoryImpl implements HelpDocsTypeFactory {

   private DocumentHolder.Callback documentCallback;

   public HelpDocsHolderFactoryImpl(DocumentHolder.Callback documentCallback) {
      this.documentCallback = documentCallback;
   }

   @Override
   public BaseHolder holder(ViewGroup parent, int viewType) {
      switch (viewType) {
         case R.layout.adapter_item_document:
            AdapterItemDocumentBinding documentBinding = DataBindingUtil
                  .bind(LayoutInflater
                        .from(parent.getContext()).inflate(viewType, parent, false));
            return new DocumentHolder(documentBinding, documentCallback);
         case R.layout.adapter_item_load_more_feed:
            AdapterItemLoadMoreFeedBinding loadMoreFeedBinding = DataBindingUtil
                  .bind(LayoutInflater
                        .from(parent.getContext()).inflate(viewType, parent, false));
            return new LoadMoreHolder(loadMoreFeedBinding);
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public int type(WalletDocument document) {
      return R.layout.adapter_item_document;
   }

   @Override
   public int type(WalletLoadMore walletLoadMore) {
      return R.layout.adapter_item_load_more_feed;
   }
}
