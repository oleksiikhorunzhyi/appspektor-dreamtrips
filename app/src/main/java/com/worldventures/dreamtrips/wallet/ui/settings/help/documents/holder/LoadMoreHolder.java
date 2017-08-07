package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.holder;

import android.view.View;

import com.worldventures.dreamtrips.databinding.AdapterItemLoadMoreFeedBinding;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.WalletLoadMore;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;

public class LoadMoreHolder extends BaseHolder<WalletLoadMore> {

   private final AdapterItemLoadMoreFeedBinding binding;

   public LoadMoreHolder(AdapterItemLoadMoreFeedBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
   }

   @Override
   public void setData(WalletLoadMore data) {
      binding.pbLoadMore.setVisibility(data.isVisible() ? View.VISIBLE : View.GONE);
   }
}
