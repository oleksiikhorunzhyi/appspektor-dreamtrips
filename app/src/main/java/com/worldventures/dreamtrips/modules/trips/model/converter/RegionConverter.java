package com.worldventures.dreamtrips.modules.trips.model.converter;

import com.worldventures.dreamtrips.api.trip.model.TripRegion;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;

import io.techery.mappery.MapperyContext;

public class RegionConverter implements Converter<TripRegion, RegionModel> {

   @Override
   public Class<TripRegion> sourceClass() {
      return TripRegion.class;
   }

   @Override
   public Class<RegionModel> targetClass() {
      return RegionModel.class;
   }

   @Override
   public RegionModel convert(MapperyContext mapperyContext, TripRegion tripRegion) {
      RegionModel regionModel = new RegionModel();
      regionModel.setId(tripRegion.id());
      regionModel.setName(tripRegion.name());
      return regionModel;
   }
}
