package com.worldventures.dreamtrips.modules.trips.model.converter;

import com.worldventures.dreamtrips.api.trip.model.TripPinWrapper;
import com.worldventures.dreamtrips.modules.common.model.Coordinates;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.trips.model.Pin;

import java.util.ArrayList;

import io.techery.mappery.MapperyContext;

public class TripPinToPinConverter implements Converter<TripPinWrapper, Pin> {

   @Override
   public Class<TripPinWrapper> sourceClass() {
      return TripPinWrapper.class;
   }

   @Override
   public Class<Pin> targetClass() {
      return Pin.class;
   }

   @Override
   public Pin convert(MapperyContext mapperyContext, TripPinWrapper tripPin) {
      Pin pin = new Pin();
      pin.setCoordinates(new Coordinates(tripPin.item().coordinates().lat(), tripPin.item().coordinates().lng()));
      pin.setTripUids(new ArrayList<>(tripPin.item().tripsUids()));
      return pin;
   }
}
