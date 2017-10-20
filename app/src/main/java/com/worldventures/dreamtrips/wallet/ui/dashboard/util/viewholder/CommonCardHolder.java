package com.worldventures.dreamtrips.wallet.ui.dashboard.util.viewholder;

import com.worldventures.dreamtrips.databinding.ItemWalletRecordBinding;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CommonCardViewModel;

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
