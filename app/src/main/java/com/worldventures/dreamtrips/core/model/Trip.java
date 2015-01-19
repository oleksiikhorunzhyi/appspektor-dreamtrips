package com.worldventures.dreamtrips.core.model;

import java.util.List;

public class Trip extends BaseEntity {
    String name;
    String description;
    boolean featured;
    boolean rewarded;
    long duration;
    boolean price_available;
    boolean available;
    long rewardsLimit;
    Price price;
    List<TripImage> images;
    Location location;
    Schedule availabilityDates;

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

    public boolean isRewarded() {
        return rewarded;
    }

    public void setRewarded(boolean rewarded) {
        this.rewarded = rewarded;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isPrice_available() {
        return price_available;
    }

    public void setPrice_available(boolean price_available) {
        this.price_available = price_available;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
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

    public List<TripImage> getImages() {
        return images;
    }

    public void setImages(List<TripImage> images) {
        this.images = images;
    }

    public Location getGeoLocation() {
        return location;
    }

    public void setGeoLocation(Location geoLocation) {
        this.location = geoLocation;
    }

    public Schedule getAvailabilityDates() {
        return availabilityDates;
    }

    public void setAvailabilityDates(Schedule availabilityDates) {
        this.availabilityDates = availabilityDates;
    }
}
