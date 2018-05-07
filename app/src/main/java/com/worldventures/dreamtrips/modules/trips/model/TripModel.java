package com.worldventures.dreamtrips.modules.trips.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.core.model.Location;
import com.worldventures.dreamtrips.social.ui.feed.model.BaseFeedEntity;

import java.io.Serializable;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class TripModel extends BaseFeedEntity implements Serializable {
   public static final String  PATTERN = "?width=%d&height=%d";
   private static final long serialVersionUID = 123L;

   private String tripId = "";
   private String name = "";
   private String description = "";
   private String thumbnailUrl;
   private List<String> imageUrls;
   private int duration = 0;
   private boolean hasMultipleDates;
   private boolean isSoldOut;
   private boolean isFeatured;
   private boolean isPlatinum;
   private boolean isInBucketList;
   private long rewardsLimit = 0L;
   private Price price;
   private Location location;
   private Schedule availabilityDates;
   private List<ContentItem> content;

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

   public String getThumbnailUrl() {
      return thumbnailUrl;
   }

   public void setThumbnailUrl(String thumbnailUrl) {
      this.thumbnailUrl = thumbnailUrl;
   }

   public List<String> getImageUrls() {
      return imageUrls;
   }

   public void setImageUrls(List<String> imageUrls) {
      this.imageUrls = imageUrls;
   }

   public int getDuration() {
      return duration;
   }

   public void setDuration(int duration) {
      this.duration = duration;
   }

   public boolean getHasMultipleDates() {
      return hasMultipleDates;
   }

   public void setHasMultipleDates(boolean hasMultipleDates) {
      this.hasMultipleDates = hasMultipleDates;
   }

   public boolean isSoldOut() {
      return isSoldOut;
   }

   public void setSoldOut(boolean soldOut) {
      isSoldOut = soldOut;
   }

   public boolean isFeatured() {
      return isFeatured;
   }

   public void setFeatured(boolean featured) {
      isFeatured = featured;
   }

   public boolean isPlatinum() {
      return isPlatinum;
   }

   public void setPlatinum(boolean platinum) {
      isPlatinum = platinum;
   }

   public boolean isInBucketList() {
      return isInBucketList;
   }

   public void setInBucketList(boolean inBucketList) {
      isInBucketList = inBucketList;
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

   public void setLocation(Location location) {
      this.location = location;
   }

   public Schedule getAvailabilityDates() {
      return availabilityDates;
   }

   public void setAvailabilityDates(Schedule availabilityDates) {
      this.availabilityDates = availabilityDates;
   }

   public List<ContentItem> getContent() {
      return content;
   }

   public void setContent(List<ContentItem> content) {
      this.content = content;
   }

   @Override
   public String place() {
      return location.getName();
   }

   public String getThumb(int size) {
      return getThumb(size, size);
   }

   public String getThumb(int width, int height) {
      return thumbnailUrl + String.format(PATTERN, width, height);
   }
}
