package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.google.android.gms.maps.model.LatLng;
import com.worldventures.dreamtrips.api.dtl.locations.model.Location;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import io.techery.mappery.MapperyContext;

import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlLocation;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

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
            .coordinates(location.coordinates() != null ? new LatLng(location.coordinates().lat(), location.coordinates().lng()) : null)
            .build();
   }
}
