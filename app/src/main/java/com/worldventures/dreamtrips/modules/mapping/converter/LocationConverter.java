package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.modules.trips.model.Location;

import io.techery.mappery.MapperyContext;

public class LocationConverter implements Converter<com.worldventures.dreamtrips.api.post.model.response.Location, com.worldventures.dreamtrips.modules.trips.model.Location> {

   @Override
   public Location convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.post.model.response.Location apiLocation) {
      Location location = new Location();
      location.setName(apiLocation.name());
      location.setLat(apiLocation.lat());
      location.setLng(apiLocation.lng());
      return location;
   }

   @Override
   public Class<com.worldventures.dreamtrips.api.post.model.response.Location> sourceClass() {
      return com.worldventures.dreamtrips.api.post.model.response.Location.class;
   }

   @Override
   public Class<Location> targetClass() {
      return Location.class;
   }
}
