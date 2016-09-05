package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.innahema.collections.query.functions.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Coordinates;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableCoordinates;

public class CoordinatesMapper implements Converter<com.worldventures.dreamtrips.api.dtl.locations.model.Coordinates, Coordinates> {

   public static final CoordinatesMapper INSTANCE = new CoordinatesMapper();

   @Override
   public Coordinates convert(com.worldventures.dreamtrips.api.dtl.locations.model.Coordinates source) {
      return ImmutableCoordinates.builder().lat(source.lat()).lng(source.lng()).build();
   }
}
