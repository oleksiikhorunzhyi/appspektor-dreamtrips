package com.worldventures.dreamtrips.wallet.ui.dashboard.util.viewholder;


import com.worldventures.dreamtrips.databinding.CardGroupNameItemBindingBinding;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.BaseHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CardGroupHeaderModel;

public class CardGroupHeaderHolder extends BaseHolder<CardGroupHeaderModel> {

   private CardGroupNameItemBindingBinding binding;

   public CardGroupHeaderHolder(CardGroupNameItemBindingBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
   }

   @Override
   public void setData(CardGroupHeaderModel data) {
      this.binding.setTitle(data);
   }
}
