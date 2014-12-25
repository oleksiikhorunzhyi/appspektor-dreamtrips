package com.worldventures.dreamtrips.core.model;

public class Trip extends BaseEntity {
    String name;
    String description;
    String location;
    boolean isFeatured;
    boolean isRewarded;
    long duration;
    boolean isPriceAvailable;
    long rewardsLimit;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public boolean isRewarded() {
        return isRewarded;
    }

    public void setRewarded(boolean isRewarded) {
        this.isRewarded = isRewarded;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isPriceAvailable() {
        return isPriceAvailable;
    }

    public void setPriceAvailable(boolean isPriceAvalible) {
        this.isPriceAvailable = isPriceAvalible;
    }

    public long getRewardsLimit() {
        return rewardsLimit;
    }

    public void setRewardsLimit(long rewardsLimit) {
        this.rewardsLimit = rewardsLimit;
    }
}
