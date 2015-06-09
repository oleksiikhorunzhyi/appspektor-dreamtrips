package com.worldventures.dreamtrips.modules.trips.presenter;

import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.DateFilterItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.Icepick;
import icepick.Icicle;

public class BaseTripsPresenter<T extends Presenter.View> extends Presenter<T> {

    @Inject
    protected SnappyRepository db;

    @Icicle
    double maxPrice;
    @Icicle
    double minPrice;
    @Icicle
    int maxNights;
    @Icicle
    int minNights;
    @Icicle
    boolean showSoldOut;
    @Icicle
    boolean showFavorites;
    @Icicle
    boolean showRecentlyAdded;
    @Icicle
    DateFilterItem dateFilterItem;
    @Icicle
    ArrayList<Integer> acceptedRegions;
    @Icicle
    ArrayList<ActivityModel> acceptedThemes;

    protected ArrayList<TripModel> cachedTrips = new ArrayList<>();

    @Override
    public void restoreInstanceState(Bundle savedState) {
        super.restoreInstanceState(savedState);
        if (savedState != null) {
            Icepick.restoreInstanceState(this, savedState);
        } else {
            maxPrice  = Double.MAX_VALUE;
            maxNights = Integer.MAX_VALUE;
            dateFilterItem = new DateFilterItem();
        }
    }

    @Override
    public void saveInstanceState(Bundle outState) {
        super.saveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    protected ArrayList<TripModel> performFiltering(List<TripModel> trips) {
        ArrayList<TripModel> filteredTrips = new ArrayList<>();
        filteredTrips.addAll(Queryable.from(trips).filter(input ->
                        input.isPriceAccepted(maxPrice, minPrice)
                                && input.isDurationAccepted(maxNights, minNights, dateFilterItem)
                                && input.isCategoriesAccepted(acceptedThemes, acceptedRegions)
                                && (showSoldOut || input.isSoldOut())
                                && (!showFavorites || input.isLiked())
                                && (!showRecentlyAdded || input.isRecentlyAdded())
        ).toList());

        return filteredTrips;
    }

    public void resetFilters() {
        this.maxNights = Integer.MAX_VALUE;
        this.maxPrice = Double.MAX_VALUE;
        this.minPrice = 0;
        this.minNights = 0;
        this.showFavorites = false;
        this.showSoldOut = false;
        this.acceptedRegions = null;
        this.acceptedThemes = null;
        dateFilterItem.reset();
    }

    public void setFilters(FilterBusEvent event) {
        if (event == null || event.isReset()) {
            resetFilters();
        } else {
            maxPrice = event.getMaxPrice();
            minNights = event.getMinNights();
            minPrice = event.getMinPrice();
            maxNights = event.getMaxNights();
            acceptedRegions = event.getAcceptedRegions();
            acceptedThemes = event.getAcceptedActivities();
            showSoldOut = event.isShowSoldOut();
            showFavorites = event.isShowFavorites();
            dateFilterItem = event.getDateFilterItem();
            showRecentlyAdded = event.isShowRecentlyAdded();
        }
    }


}
