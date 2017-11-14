package com.worldventures.wallet.ui.settings.help.documents.holder;

import android.view.View;

import com.worldventures.wallet.databinding.ItemWalletLoadMoreBinding;
import com.worldventures.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.wallet.ui.settings.help.documents.model.WalletLoadMoreModel;

public class LoadMoreHolder extends BaseHolder<WalletLoadMoreModel> {

   private final ItemWalletLoadMoreBinding binding;

   public LoadMoreHolder(ItemWalletLoadMoreBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
   }

   @Override
   public void setData(WalletLoadMoreModel data) {
      binding.pbLoadMore.setVisibility(data.isVisible() ? View.VISIBLE : View.GONE);
   }
}
