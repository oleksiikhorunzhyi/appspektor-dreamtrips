package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareResponse;
import com.worldventures.dreamtrips.wallet.domain.entity.Firmware;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableFirmwareInfo;

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
      com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo firmwareInfoFromServer = firmwareResponse.firmwareInfo();
      FirmwareInfo firmwareInfo = firmwareInfoFromServer == null ? null : ImmutableFirmwareInfo.builder()
            .id(firmwareInfoFromServer.id())
            .firmwareName(firmwareInfoFromServer.firmwareName())
            .firmwareVersion(firmwareInfoFromServer.firmwareVersion())
            .sdkVersion(firmwareInfoFromServer.sdkVersion())
            .fileSize(firmwareInfoFromServer.fileSize())
            .releaseNotes(firmwareInfoFromServer.releaseNotes())
            .isCompatible(firmwareInfoFromServer.isCompatible())
            .url(firmwareInfoFromServer.url())
            .build();

      return ImmutableFirmware.builder()
            .firmwareInfo(firmwareInfo)
            .updateAvailable(firmwareResponse.updateAvailable())
            .build();
   }
}