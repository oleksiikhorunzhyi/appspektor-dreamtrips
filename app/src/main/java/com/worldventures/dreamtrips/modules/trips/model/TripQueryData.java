package com.worldventures.dreamtrips.modules.trips.model;

import android.text.TextUtils;

import com.worldventures.dreamtrips.core.utils.DateTimeUtils;

import java.util.Date;
import java.util.List;

public class TripQueryData {

    int page;
    int perPage;
    String query;
    long durationMin;
    long durationMax;
    double priceMin;
    double priceMax;
    Date startDate;
    Date endDate;
    List<Integer> regions;
    List<Integer> activities;
    boolean soldOut;
    boolean recent;
    boolean liked;

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
        return DateTimeUtils.convertDateToUTCString(startDate);
    }

    public String endDate() {
        return DateTimeUtils.convertDateToUTCString(endDate);
    }

    public String regions() {
        return TextUtils.join(",", regions);
    }

    public String activities() {
        return TextUtils.join(",", activities);
    }

    public int soldOut() {
        return soldOut ? 1 : 0;
    }

    public int recent() {
        return recent ? 1 : 0;
    }

    public int liked() {
        return liked ? 1 : 0;
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

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setRegions(List<Integer> regions) {
        this.regions = regions;
    }

    public void setActivities(List<Integer> activities) {
        this.activities = activities;
    }

    public void setSoldOut(boolean soldOut) {
        this.soldOut = soldOut;
    }

    public void setRecent(boolean recent) {
        this.recent = recent;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
