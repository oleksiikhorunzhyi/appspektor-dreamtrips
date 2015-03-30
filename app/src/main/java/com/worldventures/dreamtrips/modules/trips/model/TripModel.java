package com.worldventures.dreamtrips.modules.trips.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;
import com.worldventures.dreamtrips.modules.common.view.util.Filterable;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;

import java.util.ArrayList;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class TripModel extends BaseEntity implements Filterable {
    private String name;
    private String description;
    private boolean featured;
    private boolean rewarded;
    private boolean liked;
    private int duration;
    private boolean price_available;
    private boolean available;
    private long rewardsLimit;
    private Price price;
    private Location location;
    private Schedule dates;
    private RegionModel region;
    private List<TripImage> images;
    private List<ActivityModel> activities;

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

    @Override
    public boolean containsQuery(String query) {
        return query == null || name.toLowerCase().contains(query) || location.getName().toLowerCase().contains(query);
    }
}
