package com.worldventures.dreamtrips.modules.trips.model;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public interface TripQueryData {

    int getPage();

    int getPerPage();

    @Nullable String getQuery();

    @Nullable Integer getDurationMin();

    @Nullable Integer getDurationMax();

    @Nullable Double getPriceMin();

    @Nullable Double getPriceMax();

    @Nullable String getStartDate();

    @Nullable String getEndDate();

    @Nullable String getRegions();

    @Nullable String getActivities();

    int isSoldOut();

    int isRecent();

    int isLiked();
}
