package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.holder;

import android.view.View;

import com.worldventures.dreamtrips.databinding.AdapterItemLoadMoreFeedBinding;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.model.WalletLoadMoreModel;

public class LoadMoreHolder extends BaseHolder<WalletLoadMoreModel> {

   private final AdapterItemLoadMoreFeedBinding binding;

   public LoadMoreHolder(AdapterItemLoadMoreFeedBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
   }

   @Override
   public void setData(WalletLoadMoreModel data) {
      binding.pbLoadMore.setVisibility(data.isVisible() ? View.VISIBLE : View.GONE);
   }
}
