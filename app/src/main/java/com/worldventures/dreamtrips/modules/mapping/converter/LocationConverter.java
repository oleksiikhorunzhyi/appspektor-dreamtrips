package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.modules.trips.model.Location;

import io.techery.mappery.MapperyContext;

public class LocationConverter implements Converter<com.worldventures.dreamtrips.api.post.model.response.Location, Location> {

   @Override
   public Location convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.post.model.response.Location apiLocation) {
      if (apiLocation == null) return null;
      Location location = new Location();
      if (apiLocation.lat() != null) {
         location.setLat(apiLocation.lat());
      }
      if (apiLocation.lng() != null) {
         location.setLng(apiLocation.lng());
      }
      location.setName(apiLocation.name());
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
