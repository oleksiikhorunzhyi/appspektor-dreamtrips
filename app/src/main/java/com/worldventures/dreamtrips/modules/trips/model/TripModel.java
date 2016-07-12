package com.worldventures.dreamtrips.modules.trips.model;

import android.content.res.Resources;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.gson.annotations.SerializedName;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.util.Filterable;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedEntity;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class TripModel extends BaseFeedEntity implements Filterable {
    public static final String PATTERN = "?width=%d&height=%d";

    public static final long serialVersionUID = 123L;

    @SerializedName("trip_id")
    private String tripId;

    private String name;
    private String description;
    private boolean featured;
    private boolean rewarded;
    private int duration;
    @SerializedName("price_available")
    private boolean priceAvailable;
    private boolean available;
    private boolean hasMultipleDates;
    @SerializedName("sold_out")
    private boolean soldOut;
    private long rewardsLimit;
    private Price price;
    private Location location;
    private Schedule dates;
    private RegionModel region;
    private List<TripImage> images;
    private List<ActivityModel> activities;
    private boolean platinum;
    @SerializedName("rewards_rules")
    private RewardsRuleModel rewardsRules;
    @SerializedName("recent")
    private boolean recentlyAdded;
    private boolean inBucketList;

    @Override
    public String place() {
        return location != null ? location.getName() : null;
    }

    public String getTripId() {
        return tripId;
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

    public boolean isSoldOut() {
        return soldOut;
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

    public boolean isHasMultipleDates() {
        return hasMultipleDates;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getRewardsLimit(User user) {
        String result = String.valueOf(rewardsLimit);

        if (rewardsRules != null) {
            if (user.isPlatinum() && rewardsRules.hasDtp()) {
                result = rewardsRules.getDtp();
            } else if (user.isGold() && rewardsRules.hasDtg()) {
                result = rewardsRules.getDtg();
            } else if (user.isGeneral() && rewardsRules.hasDtm()) {
                result = rewardsRules.getDtm();
            }
        }

        return result;
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

    public RegionModel getRegion() {
        return region;
    }

    public void setRegion(RegionModel region) {
        this.region = region;
    }


    public boolean isRecentlyAdded() {
        return recentlyAdded;
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
        String url = getImageUrl("THUMB");
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.tripImageHeight);
        return url + String.format(PATTERN,
                dimensionPixelSize, dimensionPixelSize);
    }

    public String getThumb(int width, int height) {
        return getImageUrl("THUMB") + String.format(PATTERN, width, height);
    }

    public List<ActivityModel> getActivities() {
        return activities;
    }

    public List<TripImage> getImages() {
        return images;
    }

    public List<TripImage> getFilteredImages() {
        List<TripImage> filteredImages = new ArrayList<>();
        filteredImages.addAll(getFilteredImagesByTag("RETINA"));

        if (filteredImages.isEmpty()) {
            filteredImages.addAll(getFilteredImagesByTag("NORMAL"));
        }

        return filteredImages;
    }

    private List<TripImage> getFilteredImagesByTag(String tag) {
        return Queryable.from(images).filter(input ->
                tag.equals(input.getType())).toList();
    }

    public long getStartDateMillis() {
        return dates.getStartDate().getTime();
    }

    public boolean isPriceAccepted(double maxPrice, double minPrice) {
        return price.getAmount() <= maxPrice &&
                price.getAmount() >= minPrice;
    }

    public boolean isDurationAccepted(int maxNights, int minNights, DateFilterItem dateFilterItem) {
        return duration <= maxNights &&
                duration >= minNights &&
                getAvailabilityDates().check(dateFilterItem);
    }


    public boolean isCategoriesAccepted(List<ActivityModel> acceptedThemes, List<Integer> acceptedRegions) {
        return themesAccepted(acceptedThemes)
                && regionsAccepted(acceptedRegions);
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

    public boolean isInBucketList() {
        return inBucketList;
    }

    public void setInBucketList(boolean inBucketList) {
        this.inBucketList = inBucketList;
    }

    @Override
    public boolean containsQuery(String query) {
        return name == null || location == null ||
                query == null || name.toLowerCase().contains(query) ||
                location.getName().toLowerCase().contains(query);
    }

    @Override
    public String toString() {
        return tripId;
    }

}
