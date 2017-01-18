package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareVersions

class TestFirmwareInfo : FirmwareInfo {

   override fun firmwareVersion(): String {
      return "1.0.1-test"
   }

   override fun id(): String {
      return ""
   }

   override fun url(): String {
      return ""
   }

   override fun firmwareVersions(): FirmwareVersions {
      return object : FirmwareVersions {
         override fun puckAtmelVerstion() = "1.0.1-test"

         override fun atmelVersion() = "1.0.1-test"

         override fun nordicVersion() = "1.0.1-test"

         override fun bootloaderNordicVersion() = "1.0.1-test"

      }
   }

   override fun isCompatible(): Boolean {
      return true
   }

   override fun sdkVersion(): String {
      return "1.0.0-test"
   }

   override fun firmwareName(): String {
      return "New Firmware"
   }

   override fun releaseNotes(): String {
      return "RELEASE NOTES"
   }

   override fun fileSize(): Int {
      return 1024
   }

}