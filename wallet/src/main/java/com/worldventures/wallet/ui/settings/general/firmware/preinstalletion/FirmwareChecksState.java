package com.worldventures.wallet.ui.settings.general.firmware.preinstalletion;

public class FirmwareChecksState {
   public final boolean bluetoothEnable;
   public final boolean cardConnected;
   public final boolean charged;
   public final boolean cardInCharger;
   public final boolean cardInChargerRequired;

   public FirmwareChecksState(boolean bluetoothEnable, boolean cardConnected, boolean charged, boolean cardInChargerRequired, boolean cardInCharger) {
      this.bluetoothEnable = bluetoothEnable;
      this.cardConnected = cardConnected;
      this.charged = charged;
      this.cardInCharger = cardInCharger;
      this.cardInChargerRequired = cardInChargerRequired;
   }
}
