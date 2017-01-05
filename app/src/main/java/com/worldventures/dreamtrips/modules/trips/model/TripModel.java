package com.worldventures.dreamtrips.modules.trips.model;

import android.content.res.Resources;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedEntity;
import com.worldventures.dreamtrips.modules.trips.model.filter.DateFilterItem;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class TripModel extends BaseFeedEntity {

   public static final String RETINA = "RETINA";
   public static final String NORMAL = "NORMAL";
   public static final String THUMB = "THUMB";
   public static final String PATTERN = "?width=%d&height=%d";

   public static final long serialVersionUID = 123L;

   private String tripId;
   private String name;
   private String description;
   private boolean featured;
   private boolean rewarded;
   private int duration;
   private boolean available;
   private boolean hasMultipleDates;
   private boolean soldOut;
   private long rewardsLimit;
   private Price price;
   private Location location;
   private Schedule dates;
   private RegionModel region;
   private List<TripImage> images;
   private List<ActivityModel> activities;
   private boolean platinum;
   private boolean recent;
   private boolean inBucketList;
   private List<ContentItem> content;

   @Override
   public String place() {
      return location != null ? location.getName() : null;
   }

   public String getTripId() {
      return tripId;
   }

   public void setTripId(String tripId) {
      this.tripId = tripId;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public boolean isFeatured() {
      return featured;
   }

   public void setFeatured(boolean featured) {
      this.featured = featured;
   }

   public void setLocation(Location location) {
      this.location = location;
   }

   public void setDates(Schedule dates) {
      this.dates = dates;
   }

   public boolean isRewarded() {
      return rewarded;
   }

   public void setRewarded(boolean rewarded) {
      this.rewarded = rewarded;
   }

   public boolean isSoldOut() {
      return soldOut;
   }

   public void setSoldOut(boolean soldOut) {
      this.soldOut = soldOut;
   }

   public int getDuration() {
      return duration;
   }

   public void setDuration(int duration) {
      this.duration = duration;
   }

   public boolean isAvailable() {
      return available;
   }

   public void setAvailable(boolean available) {
      this.available = available;
   }

   public boolean hasMultipleDates() {
      return hasMultipleDates;
   }

   public void setHasMultipleDates(boolean hasMultipleDates) {
      this.hasMultipleDates = hasMultipleDates;
   }

   public long getRewardsLimit() {
      return rewardsLimit;
   }

   public void setRewardsLimit(long rewardsLimit) {
      this.rewardsLimit = rewardsLimit;
   }

   public Price getPrice() {
      return price;
   }

   public void setPrice(Price price) {
      this.price = price;
   }

   public Location getLocation() {
      return location;
   }

   public Schedule getAvailabilityDates() {
      return dates;
   }

   public RegionModel getRegion() {
      return region;
   }

   public void setRegion(RegionModel region) {
      this.region = region;
   }

   public boolean isRecentlyAdded() {
      return recent;
   }

   public void setRecentlyAdded(boolean recentlyAdded) {
      this.recent = recentlyAdded;
   }

   public List<ContentItem> getContent() {
      return content;
   }

   public void setContent(List<ContentItem> content) {
      this.content = content;
   }

   public String getImageUrl(String type) {
      String url = "";
      if (images != null) {
         for (TripImage image : images) {
            if (image.getType().equals(type)) {
               url = image.getUrl();
            }
         }
      }
      return url;
   }

   public String getThumb(Resources resources) {
      String url = getImageUrl(THUMB);
      int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.tripImageHeight);
      return url + String.format(PATTERN, dimensionPixelSize, dimensionPixelSize);
   }

   public String getThumb(int width, int height) {
      return getImageUrl(THUMB) + String.format(PATTERN, width, height);
   }

   public List<ActivityModel> getActivities() {
      return activities;
   }

   public void setActivities(List<ActivityModel> activities) {
      this.activities = activities;
   }

   public List<TripImage> getImages() {
      return images;
   }

   public void setImages(List<TripImage> images) {
      this.images = images;
   }

   public List<TripImage> getFilteredImages() {
      List<TripImage> filteredImages = new ArrayList<>();
      filteredImages.addAll(getFilteredImagesByTag(RETINA));

      if (filteredImages.isEmpty()) {
         filteredImages.addAll(getFilteredImagesByTag(NORMAL));
      }

      return filteredImages;
   }

   private List<TripImage> getFilteredImagesByTag(String tag) {
      return Queryable.from(images).filter(input -> tag.equals(input.getType())).toList();
   }

   public long getStartDateMillis() {
      return dates.getStartDate().getTime();
   }

   public boolean isPriceAccepted(double maxPrice, double minPrice) {
      return price.getAmount() <= maxPrice && price.getAmount() >= minPrice;
   }

   public boolean isDurationAccepted(int maxNights, int minNights, DateFilterItem dateFilterItem) {
      return duration <= maxNights &&
            duration >= minNights &&
            getAvailabilityDates().check(dateFilterItem);
   }

   public boolean isCategoriesAccepted(List<ActivityModel> acceptedThemes, List<Integer> acceptedRegions) {
      return themesAccepted(acceptedThemes) && regionsAccepted(acceptedRegions);
   }

   private boolean themesAccepted(List<ActivityModel> acceptedThemes) {
      return acceptedThemes == null || !isActivitiesEmpty() && !Collections.disjoint(acceptedThemes, getActivities());
   }

   private boolean regionsAccepted(List<Integer> acceptedRegions) {
      return acceptedRegions == null || getRegion() != null && acceptedRegions.contains(getRegion().getId());
   }

   private boolean isActivitiesEmpty() {
      return getActivities().isEmpty();
   }

   public boolean isPlatinum() {
      return platinum;
   }

   public void setPlatinum(boolean platinum) {
      this.platinum = platinum;
   }

   public boolean isInBucketList() {
      return inBucketList;
   }

   public void setInBucketList(boolean inBucketList) {
      this.inBucketList = inBucketList;
   }

   @Override
   public String toString() {
      return tripId;
   }
}
