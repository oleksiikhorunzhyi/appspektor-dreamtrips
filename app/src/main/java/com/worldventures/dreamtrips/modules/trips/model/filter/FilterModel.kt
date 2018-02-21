package com.worldventures.dreamtrips.modules.trips.model.filter

class FilterModel(tripsFilterData: TripsFilterData) {

   var indexLeftPrice = (tripsFilterData.minPrice / 100).toInt() - 1
   var indexRightPrice = (tripsFilterData.maxPrice / 100).toInt() - 1
   var indexLeftDuration = tripsFilterData.minNights
   var indexRightDuration = tripsFilterData.maxNights
}
