package com.worldventures.dreamtrips.wallet.ui.dashboard.util.model;


import android.content.Context;
import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.HolderTypeFactory;

public class CardGroupHeaderModel extends BaseViewModel {

   private CommonCardViewModel.StackType name;

   public CardGroupHeaderModel(CommonCardViewModel.StackType name) {
      this.name = name;
      modelId = name.toString();
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

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {dest.writeInt(this.name == null ? -1 : this.name.ordinal());}

   protected CardGroupHeaderModel(Parcel in) {
      int tmpName = in.readInt();
      this.name = tmpName == -1 ? null : CommonCardViewModel.StackType.values()[tmpName];
   }

   public static final Creator<CardGroupHeaderModel> CREATOR = new Creator<CardGroupHeaderModel>() {
      @Override
      public CardGroupHeaderModel createFromParcel(Parcel source) {return new CardGroupHeaderModel(source);}

      @Override
      public CardGroupHeaderModel[] newArray(int size) {return new CardGroupHeaderModel[size];}
   };
}
