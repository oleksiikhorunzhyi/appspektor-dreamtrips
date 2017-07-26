package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install;

import android.os.Parcel;

import com.worldventures.dreamtrips.wallet.ui.common.viewstate.WalletViewState;

public class WalletInstallFirmwareState extends WalletViewState<WalletInstallFirmwareScreen> {

   public boolean started;

   public WalletInstallFirmwareState() {
   }

   private WalletInstallFirmwareState(Parcel source) {
      started = source.readInt() == 1;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(started ? 1 : 0);
   }

   public static final Creator<WalletInstallFirmwareState> CREATOR = new Creator<WalletInstallFirmwareState>() {
      @Override
      public WalletInstallFirmwareState createFromParcel(Parcel source) {
         return new WalletInstallFirmwareState(source);
      }

      @Override
      public WalletInstallFirmwareState[] newArray(int size) {
         return new WalletInstallFirmwareState[size];
      }
   };
}
