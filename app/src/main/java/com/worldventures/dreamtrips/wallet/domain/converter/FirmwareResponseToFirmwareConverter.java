package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.wallet.domain.entity.Firmware;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareResponse;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableFirmware;

import io.techery.mappery.MapperyContext;

public class FirmwareResponseToFirmwareConverter implements com.worldventures.dreamtrips.modules.mapping.converter.Converter<FirmwareResponse, Firmware> {

   @Override
   public Class<FirmwareResponse> sourceClass() {
      return FirmwareResponse.class;
   }

   @Override
   public Class<Firmware> targetClass() {
      return Firmware.class;
   }

   @Override
   public Firmware convert(MapperyContext mapperyContext, FirmwareResponse firmwareResponse) {
      return ImmutableFirmware.builder()
            .firmwareInfo(firmwareResponse.firmwareInfo())
            .updateAvailable(firmwareResponse.updateAvailable())
            .build();
   }
}