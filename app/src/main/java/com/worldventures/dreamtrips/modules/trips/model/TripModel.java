package com.worldventures.dreamtrips.modules.trips.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.gson.annotations.SerializedName;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.view.util.Filterable;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class TripModel implements Filterable, Serializable {
    private String id;
    private String name;
    private String description;
    private boolean featured;
    private boolean rewarded;
    private boolean liked;
    private int duration;
    @SerializedName("price_available")
    private boolean priceAvailable;
    private boolean available;
    private long rewardsLimit;
    private Price price;
    private Location location;
    private Schedule dates;
    private RegionModel region;
    private List<TripImage> images;
    private List<ActivityModel> activities;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isPriceAvailable() {
        return priceAvailable;
    }

    public void setPriceAvailable(boolean priceAvailable) {
        this.priceAvailable = priceAvailable;
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

    public RegionModel getRegion() {
        return region;
    }

    public void setRegion(RegionModel region) {
        this.region = region;
    }

    public String getImageUrl(String type) {
        String url = null;
        if (images != null) {
            for (TripImage image : images) {
                if (image.getType().equals(type)) {
                    url = image.getUrl();
                }
            }
        }
        return url;
    }

    public List<ActivityModel> getActivities() {
        return activities;
    }

    public List<TripImage> getImages() {
        return images;
    }

    public List<Object> getFilteredImages() {
        List<Object> filteredImages = new ArrayList<>();
        filteredImages.addAll(Queryable.from(images).filter(input ->
                "RETINA".equals(input.getType())).toList());
        return filteredImages;
    }

    public long getStartDateMillis() {
        return dates.getStartDate().getTime();
    }

    public boolean isPriceAccepted(double maxPrice, double minPrice) {
        return priceAvailable &&
                price.getAmount() <= maxPrice &&
                price.getAmount() >= minPrice;
    }

    public boolean isDurationAccepted(int maxNights, int minNights, DateFilterItem dateFilterItem) {
        return duration <= maxNights &&
                duration >= minNights &&
                getAvailabilityDates().check(dateFilterItem);
    }

    public boolean isCategoriesAccepted(List<ActivityModel> acceptedThemes, List<Integer> acceptedRegions) {
        return (acceptedThemes == null || !Collections.disjoint(acceptedThemes, getActivities()))
                && (acceptedRegions == null || acceptedRegions.contains(getRegion().getId()));
    }

    @Override
    public boolean containsQuery(String query) {
        return query == null || name.toLowerCase().contains(query) || location.getName().toLowerCase().contains(query);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TripModel tripModel = (TripModel) o;

        if (id != null ? !id.equals(tripModel.id) : tripModel.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
