package com.worldventures.dreamtrips.wallet.ui.settings.disabledefaultcard;

import android.os.Parcel;

import com.worldventures.dreamtrips.wallet.ui.common.viewstate.WalletViewState;

public class WalletDisableDefaultCardState extends WalletViewState<WalletDisableDefaultCardPresenter.Screen> {
   private boolean delayWasChanged = false;
   private long newDisableDelay;

   public WalletDisableDefaultCardState() {
   }

   public WalletDisableDefaultCardState(Parcel in) {
      this.delayWasChanged = in.readByte() != 0;
      this.newDisableDelay = in.readLong();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeByte((byte) (this.delayWasChanged ? 1 : 0));
      dest.writeLong(this.newDisableDelay);
   }

   public void setDelayWasChanged(boolean delayWasChanged) {
      this.delayWasChanged = delayWasChanged;
   }

   public void setNewDisableDelay(long newDisableDelay) {
      this.newDisableDelay = newDisableDelay;
   }

   public boolean delayWasChanged() {
      return delayWasChanged;
   }

   public long getNewDisableDelay() {
      return newDisableDelay;
   }

   public static final Creator<WalletDisableDefaultCardState> CREATOR = new Creator<WalletDisableDefaultCardState>() {
      @Override
      public WalletDisableDefaultCardState createFromParcel(Parcel source) {
         return new WalletDisableDefaultCardState(source);
      }

      @Override
      public WalletDisableDefaultCardState[] newArray(int size) {
         return new WalletDisableDefaultCardState[size];
      }
   };
}
