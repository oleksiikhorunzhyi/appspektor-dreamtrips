package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.api.post.model.response.ImmutableLocation;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import io.techery.mappery.MapperyContext;

public class ReverseLocationConverter implements Converter<Location, com.worldventures.dreamtrips.api.post.model.response.Location> {

   @Override
   public com.worldventures.dreamtrips.api.post.model.response.Location convert(MapperyContext mapperyContext, Location location) {
      return ImmutableLocation.builder()
            .name(location.getName())
            .lat(location.getLat())
            .lng(location.getLng())
            .build();
   }

   @Override
   public Class<com.worldventures.dreamtrips.api.post.model.response.Location> targetClass() {
      return com.worldventures.dreamtrips.api.post.model.response.Location.class;
   }

   @Override
   public Class<Location> sourceClass() {
      return Location.class;
   }
}
