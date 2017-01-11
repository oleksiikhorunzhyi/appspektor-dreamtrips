package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware

class TestFirmware : SmartCardFirmware {

   override fun firmwareVersion() = "1.0.0-test"

   override fun nordicAppVersion() = "1.0.0-test"

   override fun nrfBootloaderVersion() = "1.0.0-test"

   override fun internalAtmelVersion() = "1.0.0-test"

   override fun internalAtmelBootloaderVersion() = "1.0.0-test"

   override fun externalAtmelVersion() = "1.0.0-test"

   override fun externalAtmelBootloaderVersion() = "1.0.0-test"
}