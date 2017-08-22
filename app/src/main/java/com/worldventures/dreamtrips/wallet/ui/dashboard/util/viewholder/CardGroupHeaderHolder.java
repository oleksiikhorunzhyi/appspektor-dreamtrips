package com.worldventures.dreamtrips.wallet.ui.dashboard.util.viewholder;

import com.worldventures.dreamtrips.databinding.ItemWalletCardGroupNameBinding;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CardGroupHeaderModel;

public class CardGroupHeaderHolder extends BaseHolder<CardGroupHeaderModel> {

   private ItemWalletCardGroupNameBinding binding;

   public CardGroupHeaderHolder(ItemWalletCardGroupNameBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
   }

   @Override
   public void setData(CardGroupHeaderModel data) {
      this.binding.setTitle(data);
   }
}
