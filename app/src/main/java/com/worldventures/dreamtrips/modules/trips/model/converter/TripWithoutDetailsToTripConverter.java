package com.worldventures.dreamtrips.modules.trips.model.converter;

import com.worldventures.dreamtrips.api.trip.model.TripWithoutDetails;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import io.techery.mappery.MapperyContext;

public class TripWithoutDetailsToTripConverter extends ApiTripToTripConverter<TripWithoutDetails> {

   @Override
   public Class<TripWithoutDetails> sourceClass() {
      return TripWithoutDetails.class;
   }

   @Override
   public TripModel convert(MapperyContext mapperyContext, TripWithoutDetails tripWithoutDetails) {
      return convertTrip(mapperyContext, tripWithoutDetails);
   }
}
