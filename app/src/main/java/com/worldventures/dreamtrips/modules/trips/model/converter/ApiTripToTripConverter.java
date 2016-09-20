package com.worldventures.dreamtrips.modules.trips.model.converter;

import com.worldventures.dreamtrips.api.trip.model.Trip;
import com.worldventures.dreamtrips.api.trip.model.TripDates;
import com.worldventures.dreamtrips.api.trip.model.TripLocation;
import com.worldventures.dreamtrips.api.trip.model.TripPrice;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.trips.model.Price;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;
import com.worldventures.dreamtrips.modules.trips.model.Schedule;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;

import io.techery.mappery.MapperyContext;

public abstract class ApiTripToTripConverter<T extends Trip> implements Converter<T, TripModel> {

   @Override
   public Class<TripModel> targetClass() {
      return TripModel.class;
   }

   protected TripModel convertTrip(MapperyContext mapperyContext, Trip trip) {
      TripModel tripModel = new TripModel();
      tripModel.setUid(trip.uid());
      tripModel.setLiked(trip.liked());
      tripModel.setLikesCount(trip.likes());
      tripModel.setTripId(trip.tripId());
      tripModel.setName(trip.name());
      tripModel.setDescription(trip.description());
      if (trip.images() != null)  tripModel.setImages(mapperyContext.convert(trip.images(), TripImage.class));
      tripModel.setAvailable(trip.available());
      tripModel.setFeatured(trip.featured());
      tripModel.setPlatinum(trip.platinum());
      tripModel.setPrice(price(trip.price()));
      tripModel.setRewarded(trip.rewarded());
      tripModel.setRewardsLimit(trip.rewardsLimit());
      tripModel.setSoldOut(trip.soldOut());
      tripModel.setLocation(location(trip.location()));
      if (trip.region() != null) tripModel.setRegion(mapperyContext.convert(trip.region(), RegionModel.class));
      if (trip.activities() != null) tripModel.setActivities(mapperyContext.convert(trip.activities(), ActivityModel.class));
      tripModel.setDuration(trip.duration());
      tripModel.setHasMultipleDates(trip.hasMultipleDates());
      tripModel.setDates(dates(trip.dates()));
      tripModel.setRecentlyAdded(trip.recentlyAdded());
      tripModel.setInBucketList(trip.inBucketList());
      return tripModel;
   }

   private Schedule dates(TripDates tripDates) {
      Schedule schedule = new Schedule();
      schedule.setStartDate(tripDates.startOn());
      schedule.setEndDate(tripDates.endOn());
      return schedule;
   }

   private Location location(TripLocation tripLocation) {
      Location location = new Location(tripLocation.lat(), tripLocation.lng());
      location.setName(tripLocation.name());
      return location;
   }

   private Price price(TripPrice tripPrice) {
      Price price = new Price();
      price.setAmount(tripPrice.amount());
      price.setCurrency(tripPrice.currency());
      return price;
   }
}
