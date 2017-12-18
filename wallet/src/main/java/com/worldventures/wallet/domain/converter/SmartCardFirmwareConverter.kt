package com.worldventures.wallet.domain.converter

import com.worldventures.core.converter.Converter
import com.worldventures.wallet.domain.entity.SmartCardFirmware
import io.techery.janet.smartcard.model.FirmwareVersion
import io.techery.mappery.MapperyContext

internal class SmartCardFirmwareConverter : Converter<FirmwareVersion, SmartCardFirmware> {

   override fun convert(context: MapperyContext, source: FirmwareVersion): SmartCardFirmware {
      return SmartCardFirmware(
            nordicAppVersion = source.appFirmwareVersion(),
            nrfBootloaderVersion = source.appBootloaderVersion(),
            internalAtmelVersion = source.internalAtmelVersion(),
            internalAtmelBootloaderVersion = source.internalAtmelBootloaderVersion(),
            externalAtmelVersion = source.externalAtmelVersion(),
            externalAtmelBootloaderVersion = source.externalAtmelBootloaderVersion())
   }

   override fun sourceClass() = FirmwareVersion::class.java

   override fun targetClass() = SmartCardFirmware::class.java
}
