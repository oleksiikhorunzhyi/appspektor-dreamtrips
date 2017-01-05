package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;

import io.techery.janet.smartcard.model.FirmwareVersion;
import io.techery.mappery.MapperyContext;

class SmartCardFirmwareConverter implements Converter<FirmwareVersion, SmartCardFirmware> {

   @Override
   public SmartCardFirmware convert(MapperyContext mapperyContext, FirmwareVersion firmwareVersion) {
      return ImmutableSmartCardFirmware.builder()
            .firmwareVersion(firmwareVersion.appFirmwareVersion())
            .nrfBootloaderVersion(firmwareVersion.appBootloaderVersion())
            .internalAtmelVersion(firmwareVersion.internalAtmelVersion())
            .internalAtmelBootloaderVersion(firmwareVersion.internalAtmelBootloaderVersion())
            .externalAtmelVersion(firmwareVersion.externalAtmelVersion())
            .externalAtmelBootloaderVersion(firmwareVersion.externalAtmelBootloaderVersion())
            .build();
   }

   @Override
   public Class<FirmwareVersion> sourceClass() {
      return FirmwareVersion.class;
   }

   @Override
   public Class<SmartCardFirmware> targetClass() {
      return SmartCardFirmware.class;
   }
}
