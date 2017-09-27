package com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter;

import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CardGroupHeaderModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CommonCardViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.viewholder.CardGroupHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.viewholder.CommonCardHolder;

import static android.databinding.DataBindingUtil.bind;
import static android.view.LayoutInflater.from;

public class DashboardHolderFactoryImpl implements DashboardHolderTypeFactory {

   @Override
   public int type(CommonCardViewModel commonCardViewModel) {
      return R.layout.item_wallet_record;
   }

   @Override
   public int type(CardGroupHeaderModel viewModel) {
      return R.layout.item_wallet_card_group_name;
   }

   @Override
   public BaseHolder holder(ViewGroup parent, int viewType) {
      switch (viewType) {
         case R.layout.item_wallet_record:
            return new CommonCardHolder(bind(from(parent.getContext()).inflate(viewType, parent, false)));
         case R.layout.item_wallet_card_group_name:
            return new CardGroupHeaderHolder(bind(from(parent.getContext()).inflate(viewType, parent, false)));
         default:
            throw new IllegalArgumentException();
      }
   }
}
