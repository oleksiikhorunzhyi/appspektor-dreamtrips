package com.worldventures.dreamtrips.wallet.ui.settings.general.profile;

import android.os.Parcel;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.ui.common.viewstate.WalletViewState;

public class WalletSettingsProfileState extends WalletViewState<WalletSettingsProfilePresenter.Screen> {

   private SmartCardUserPhoto userPhoto;
   private boolean profileDataIsChanged = false;

   public WalletSettingsProfileState() {}


   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeSerializable(this.userPhoto);
      dest.writeInt(this.profileDataIsChanged ? 1 : 0);
   }

   private WalletSettingsProfileState(Parcel in) {
      this.userPhoto = (SmartCardUserPhoto) in.readSerializable();
      this.profileDataIsChanged = in.readInt() == 1;
   }

   SmartCardUserPhoto getUserPhoto() {
      return userPhoto;
   }

   void setUserPhoto(SmartCardUserPhoto userPhoto) {
      this.userPhoto = userPhoto;
   }

   boolean getChangeProfileFlag() {
      return profileDataIsChanged;
   }

   void setChangeProfileFlag(boolean profileDataIsChanges) {
      this.profileDataIsChanged = profileDataIsChanges;
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
