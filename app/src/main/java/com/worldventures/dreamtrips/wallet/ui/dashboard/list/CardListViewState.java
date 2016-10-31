package com.worldventures.dreamtrips.wallet.ui.dashboard.list;

import android.os.Parcel;

import com.worldventures.dreamtrips.wallet.domain.entity.Firmware;
import com.worldventures.dreamtrips.wallet.ui.common.viewstate.WalletViewState;

class CardListViewState extends WalletViewState<CardListPresenter.Screen> {

   public Firmware firmware;

   CardListViewState() {}

   private CardListViewState(Parcel in) {
      this.firmware = (Firmware) in.readSerializable();
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeSerializable(this.firmware);
   }

   public static final Creator<CardListViewState> CREATOR = new Creator<CardListViewState>() {
      @Override
      public CardListViewState createFromParcel(Parcel source) {
         return new CardListViewState(source);
      }

      @Override
      public CardListViewState[] newArray(int size) {
         return new CardListViewState[size];
      }
   };
}
