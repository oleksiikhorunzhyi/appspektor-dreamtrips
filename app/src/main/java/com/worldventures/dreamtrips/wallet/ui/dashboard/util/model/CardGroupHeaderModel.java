package com.worldventures.dreamtrips.wallet.ui.dashboard.util.model;


import android.content.Context;
import android.databinding.BindingAdapter;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.HolderTypeFactory;

public class CardGroupHeaderModel extends BaseViewModel {

   private CommonCardViewModel.StackType name;

   public CardGroupHeaderModel(CommonCardViewModel.StackType name) {
      this.name = name;
   }

   public CommonCardViewModel.StackType getName() {
      return name;
   }

   @Override
   public int type(HolderTypeFactory typeFactory) {
      return typeFactory.type(this);
   }

   @BindingAdapter({"binding:cardType"})
   public static void setCardType(TextView view, CommonCardViewModel.StackType type) {
      Context context = view.getContext();
      view.setText(context.getString(type.equals(CommonCardViewModel.StackType.LOYALTY)
            ? R.string.wallet_loyalty_cards_title : R.string.wallet_payment_cards_title));
   }
}
