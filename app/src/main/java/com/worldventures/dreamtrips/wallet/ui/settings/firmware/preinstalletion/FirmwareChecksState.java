package com.worldventures.dreamtrips.wallet.ui.settings.firmware.preinstalletion;

class FirmwareChecksState {
   final boolean bluetoothEnable;
   final boolean cardConnected;
   final boolean charged;
   final boolean cardInCharger;
   final boolean cardInChargerRequired;

   FirmwareChecksState(boolean bluetoothEnable, boolean cardConnected, boolean charged, boolean cardInChargerRequired, boolean cardInCharger) {
      this.bluetoothEnable = bluetoothEnable;
      this.cardConnected = cardConnected;
      this.charged = charged;
      this.cardInCharger = cardInCharger;
      this.cardInChargerRequired = cardInChargerRequired;
   }
}
