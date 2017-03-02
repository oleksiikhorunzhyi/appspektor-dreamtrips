package com.worldventures.dreamtrips.wallet.ui.settings.removecards;

import android.os.Parcel;

import com.worldventures.dreamtrips.wallet.ui.common.viewstate.WalletViewState;

public class WalletAutoClearCardsState extends WalletViewState<WalletAutoClearCardsPresenter.Screen> {

   private boolean autoClearWasChanged = false;
   private long newAutoClearDelay;

   public WalletAutoClearCardsState() {
   }

   public WalletAutoClearCardsState(Parcel in) {
      this.autoClearWasChanged = in.readByte() != 0;
      this.newAutoClearDelay = in.readLong();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeByte((byte) (this.autoClearWasChanged ? 1 : 0));
      dest.writeLong(this.newAutoClearDelay);
   }

   public boolean autoClearWasChanged() {
      return autoClearWasChanged;
   }

   public long getNewAutoClearDelay() {
      return newAutoClearDelay;
   }

   public void setAutoClearWasChanged(boolean autoClearWasChanged) {
      this.autoClearWasChanged = autoClearWasChanged;
   }

   public void setNewAutoClearDelay(long newAutoClearDelay) {
      this.newAutoClearDelay = newAutoClearDelay;
   }

   public static final Creator<WalletAutoClearCardsState> CREATOR = new Creator<WalletAutoClearCardsState>() {
      @Override
      public WalletAutoClearCardsState createFromParcel(Parcel source) {
         return new WalletAutoClearCardsState(source);
      }

      @Override
      public WalletAutoClearCardsState[] newArray(int size) {
         return new WalletAutoClearCardsState[size];
      }
   };
}
