package com.worldventures.dreamtrips.core.model;

import com.google.common.collect.Collections2;
import com.worldventures.dreamtrips.view.util.Filterable;

import java.util.ArrayList;
import java.util.List;

public class Trip extends BaseEntity implements Filterable {
    String name;
    String description;
    boolean featured;
    boolean rewarded;
    boolean liked;
    int duration;
    boolean price_available;
    boolean available;
    long rewardsLimit;
    Price price;
    Location location;
    Schedule dates;
    Region region;
    List<TripImage> images;
    List<Activity> activities;

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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
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

    public Location getGeoLocation() {
        return location;
    }

    public void setGeoLocation(Location geoLocation) {
        this.location = geoLocation;
    }

    public Schedule getAvailabilityDates() {
        return dates;
    }

    public void setAvailabilityDates(Schedule availabilityDates) {
        this.dates = availabilityDates;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getImageUrl(String type) {
        String url = null;
        if (images != null) {
            for (TripImage image : images) {
                if (image.getType().equals(type)) {
                    url =  image.getOriginalUrl();
                }
            }
        }
        return url;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public List<TripImage> getImages() {
        return images;
    }

    public List<Object> getFilteredImages() {
        List<Object> filteredImages = new ArrayList<>();
        filteredImages.addAll(Collections2.filter(images, (input) ->
                input.getType().equals("RETINA")));
        return filteredImages;
    }

    @Override
    public boolean containsQuery(String query) {
        return name.toLowerCase().contains(query) || location.getName().toLowerCase().contains(query);
    }
}
