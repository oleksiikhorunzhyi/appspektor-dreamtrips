package com.worldventures.dreamtrips.modules.trips.model;

public class TripQueryData {

    int page;
    int perPage;
    String query;
    long durationMin;
    long durationMax;
    double priceMin;
    double priceMax;
    String startDate;
    String endDate;
    String regions;
    String activities;
    int soldOut;
    int recent;
    int liked;

    public int page() {
        return page;
    }

    public int perPage() {
        return perPage;
    }

    public String query() {
        return query;
    }

    public long durationMin() {
        return durationMin;
    }

    public long durationMax() {
        return durationMax;
    }

    public double priceMin() {
        return priceMin;
    }

    public double priceMax() {
        return priceMax;
    }

    public String startDate() {
        return startDate;
    }

    public String endDate() {
        return endDate;
    }

    public String regions() {
        return regions;
    }

    public String activities() {
        return activities;
    }

    public int soldOut() {
        return soldOut;
    }

    public int recent() {
        return recent;
    }

    public int liked() {
        return liked;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setPerPage(int per_page) {
        this.perPage = per_page;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setDurationMin(long durationMin) {
        this.durationMin = durationMin;
    }

    public void setDurationMax(long durationMax) {
        this.durationMax = durationMax;
    }

    public void setPriceMin(double priceMin) {
        this.priceMin = priceMin;
    }

    public void setPriceMax(double priceMax) {
        this.priceMax = priceMax;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setRegions(String regions) {
        this.regions = regions;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    public void setSoldOut(int soldOut) {
        this.soldOut = soldOut;
    }

    public void setRecent(int recent) {
        this.recent = recent;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }
}
