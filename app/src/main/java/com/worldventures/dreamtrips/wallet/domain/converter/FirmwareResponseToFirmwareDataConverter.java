package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareResponse;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableFirmwareUpdateData;

import io.techery.mappery.MapperyContext;

class FirmwareResponseToFirmwareDataConverter implements Converter<FirmwareResponse, FirmwareUpdateData> {

   @Override
   public Class<FirmwareResponse> sourceClass() {
      return FirmwareResponse.class;
   }

   @Override
   public Class<FirmwareUpdateData> targetClass() {
      return FirmwareUpdateData.class;
   }

   @Override
   public FirmwareUpdateData convert(MapperyContext mapperyContext, FirmwareResponse firmwareResponse) {
      return ImmutableFirmwareUpdateData.builder()
            .firmwareInfo(firmwareResponse.firmwareInfo())
            .updateAvailable(firmwareResponse.updateAvailable())
            .build();
   }
}