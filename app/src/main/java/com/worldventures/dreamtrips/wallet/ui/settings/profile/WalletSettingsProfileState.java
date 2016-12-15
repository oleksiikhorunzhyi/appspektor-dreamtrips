package com.worldventures.dreamtrips.wallet.ui.settings.profile;

import android.os.Parcel;

import com.worldventures.dreamtrips.wallet.ui.common.viewstate.WalletViewState;

import java.io.File;

public class WalletSettingsProfileState extends WalletViewState<WalletSettingsProfilePresenter.Screen> {

   File photo;
   String photoPath;

   public WalletSettingsProfileState() {}

   private WalletSettingsProfileState(Parcel in) {
      this.photo = (File) in.readSerializable();
      this.photoPath = in.readString();
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeSerializable(this.photo);
      dest.writeString(this.photoPath);
   }

   public static final Creator<WalletSettingsProfileState> CREATOR = new Creator<WalletSettingsProfileState>() {

      @Override
      public WalletSettingsProfileState createFromParcel(Parcel source) {
         return new WalletSettingsProfileState(source);
      }

      @Override
      public WalletSettingsProfileState[] newArray(int size) {
         return new WalletSettingsProfileState[size];
      }
   };
}
