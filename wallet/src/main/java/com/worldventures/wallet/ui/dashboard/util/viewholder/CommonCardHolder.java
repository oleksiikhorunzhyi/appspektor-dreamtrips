package com.worldventures.wallet.ui.dashboard.util.viewholder;

import com.worldventures.wallet.databinding.ItemWalletRecordBinding;
import com.worldventures.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.wallet.ui.dashboard.util.model.CommonCardViewModel;

public class CommonCardHolder extends BaseHolder<CommonCardViewModel> {

   private final ItemWalletRecordBinding binding;

   public CommonCardHolder(ItemWalletRecordBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
   }

   @Override
   public void setData(CommonCardViewModel data) {
      binding.setCardModel(data);
   }

   public CommonCardViewModel getData() {
      return binding.getCardModel();
   }
}
