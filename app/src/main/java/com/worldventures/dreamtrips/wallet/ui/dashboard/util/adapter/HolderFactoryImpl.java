package com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter;


import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.databinding.CardCellBindingBinding;
import com.worldventures.dreamtrips.databinding.CardGroupNameItemBindingBinding;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CardGroupHeaderModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CommonCardViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.viewholder.CardGroupHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.viewholder.CommonCardHolder;

public class HolderFactoryImpl implements HolderTypeFactory {

   @Override
   public int type(CommonCardViewModel commonCardViewModel) {
      return R.layout.card_cell_binding;
   }

   @Override
   public int type(CardGroupHeaderModel viewModel) {
      return R.layout.card_group_name_item_binding;
   }

   @Override
   public BaseHolder holder(ViewGroup parent, int viewType) {

      switch (viewType) {
         case R.layout.card_cell_binding:
            CardCellBindingBinding cardCellBinding = DataBindingUtil
                  .bind(LayoutInflater
                        .from(parent.getContext()).inflate(viewType, parent, false));
            return new CommonCardHolder(cardCellBinding);
         case R.layout.card_group_name_item_binding:
            CardGroupNameItemBindingBinding cardGroup = DataBindingUtil
                  .bind(LayoutInflater
                        .from(parent.getContext()).inflate(viewType, parent, false));
            return new CardGroupHeaderHolder(cardGroup);
         default:
            throw new IllegalArgumentException();

      }
   }
}
