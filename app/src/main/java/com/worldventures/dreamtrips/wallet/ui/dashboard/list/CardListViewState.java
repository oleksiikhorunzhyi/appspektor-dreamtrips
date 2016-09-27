package com.worldventures.dreamtrips.wallet.ui.dashboard.list;

import android.os.Parcel;

import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareInfo;
import com.worldventures.dreamtrips.wallet.ui.common.viewstate.WalletViewState;

public class CardListViewState extends WalletViewState<CardListPresenter.Screen> {

   public FirmwareInfo firmwareInfo;

   public CardListViewState() {}

   protected CardListViewState(Parcel in) {
      this.firmwareInfo = (FirmwareInfo) in.readSerializable();
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeSerializable(this.firmwareInfo);
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
