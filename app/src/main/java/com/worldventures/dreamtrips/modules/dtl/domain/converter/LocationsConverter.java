package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.google.android.gms.maps.model.LatLng;
import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.dtl.locations.model.Location;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlLocation;

import io.techery.mappery.MapperyContext;

public class LocationsConverter implements Converter<Location, DtlLocation> {

   @Override
   public Class<Location> sourceClass() {
      return Location.class;
   }

   @Override
   public Class<DtlLocation> targetClass() {
      return DtlLocation.class;
   }

   @Override
   public DtlLocation convert(MapperyContext mapperyContext, Location location) {
      return ImmutableDtlLocation.builder()
            .id(location.id())
            .longName(location.longName())
            .locationSourceType(LocationSourceType.EXTERNAL)
            .type(location.type())
            .locatedIn(location.locatedIn() != null ? mapperyContext.convert(location.locatedIn(), DtlLocation.class) : null)
            .coordinates(location.coordinates() != null ? new LatLng(location.coordinates()
                  .lat(), location.coordinates().lng()) : null)
            .build();
   }
}
