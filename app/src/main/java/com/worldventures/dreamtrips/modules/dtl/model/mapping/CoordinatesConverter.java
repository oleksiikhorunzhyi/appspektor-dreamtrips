package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Coordinates;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableCoordinates;

import io.techery.mappery.MapperyContext;

public class CoordinatesConverter implements Converter<com.worldventures.dreamtrips.api.dtl.locations.model.Coordinates, Coordinates> {

   @Override
   public Class<com.worldventures.dreamtrips.api.dtl.locations.model.Coordinates> sourceClass() {
      return com.worldventures.dreamtrips.api.dtl.locations.model.Coordinates.class;
   }

   @Override
   public Class<Coordinates> targetClass() {
      return Coordinates.class;
   }

   @Override
   public Coordinates convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.locations.model.Coordinates coordinates) {
      return ImmutableCoordinates.builder().lat(coordinates.lat()).lng(coordinates.lng()).build();
   }
}
