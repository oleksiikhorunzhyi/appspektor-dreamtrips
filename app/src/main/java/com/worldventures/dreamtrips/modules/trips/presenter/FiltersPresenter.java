package com.worldventures.dreamtrips.modules.trips.presenter;

import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.core.utils.events.RequestFilterDataEvent;
import com.worldventures.dreamtrips.core.utils.events.ResetFiltersEvent;
import com.worldventures.dreamtrips.core.utils.events.ToggleRegionVisibilityEvent;
import com.worldventures.dreamtrips.core.utils.events.ToggleThemeVisibilityEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.DateFilterItem;
import com.worldventures.dreamtrips.modules.trips.model.FilterFavoriteModel;
import com.worldventures.dreamtrips.modules.trips.model.FilterModel;
import com.worldventures.dreamtrips.modules.trips.model.FilterRecentlyAddedModel;
import com.worldventures.dreamtrips.modules.trips.model.FilterSoldOutModel;
import com.worldventures.dreamtrips.modules.trips.model.RegionHeaderModel;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;
import com.worldventures.dreamtrips.modules.trips.model.ThemeHeaderModel;
import com.worldventures.dreamtrips.modules.trips.model.TripsFilterDataAnalyticsWrapper;
import com.worldventures.dreamtrips.util.TripsFilterData;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;

public class FiltersPresenter extends Presenter<FiltersPresenter.View> {

    @Inject
    protected SnappyRepository db;

    @State
    ArrayList<RegionModel> regions = new ArrayList<>();
    @State
    ArrayList<ActivityModel> activities = new ArrayList<>();
    @State
    ArrayList<ActivityModel> parentActivities = new ArrayList<>();

    /**
     * variables for filtering
     */
    @State
    FilterModel filterModel;
    @State
    ThemeHeaderModel themeHeaderModel;
    @State
    RegionHeaderModel regionHeaderModel;
    @State
    FilterSoldOutModel soldOutModel;
    @State
    FilterRecentlyAddedModel recentlyAddedModel;
    @State
    FilterFavoriteModel favoriteModel;
    @State
    DateFilterItem dateFilterItem;
    @State
    TripsFilterData tripFilterData;

    @Override
    public void restoreInstanceState(Bundle savedState) {
        super.restoreInstanceState(savedState);
        if (savedState == null) {
            filterModel = new FilterModel();
            dateFilterItem = new DateFilterItem();
            themeHeaderModel = new ThemeHeaderModel();
            soldOutModel = new FilterSoldOutModel();
            favoriteModel = new FilterFavoriteModel();
            recentlyAddedModel = new FilterRecentlyAddedModel();
            regionHeaderModel = new RegionHeaderModel();
            loadFilters();
        }
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        fillData();
        if (tripFilterData == null) {
            tripFilterData = TripsFilterData.createDefault(db);
        }
    }

    public void loadFilters() {
        activities.addAll(db.readList(SnappyRepository.ACTIVITIES, ActivityModel.class));
        parentActivities.addAll(getParentActivities());
        regions.addAll(db.readList(SnappyRepository.REGIONS, RegionModel.class));
    }

    public void fillData() {
        if (regions != null && activities != null) {
            List<Object> data = new ArrayList<>();
            data.clear();
            data.add(dateFilterItem);
            data.add(filterModel);
            data.add(soldOutModel);
            data.add(favoriteModel);
            data.add(recentlyAddedModel);

            data.add(regionHeaderModel);
            if (!regionHeaderModel.isHide()) {
                data.addAll(regions);
            }

            data.add(themeHeaderModel);

            if (!themeHeaderModel.isHide()) {
                data.addAll(parentActivities);
            }

            view.getAdapter().clear();
            view.getAdapter().addItems(data);
        }
    }

    public void setRegionsChecked(boolean isChecked) {
        if (regions != null) {
            for (RegionModel region : regions) {
                region.setChecked(isChecked);
            }
        }
    }

    public void setThemesChecked(boolean isChecked) {
        if (parentActivities != null) {
            for (ActivityModel activity : parentActivities) {
                activity.setChecked(isChecked);
            }
        }
    }

    public void acceptFilters() {
        eventBus.removeStickyEvent(FilterBusEvent.class);
        eventBus.postSticky(new FilterBusEvent(tripFilterData));
        TrackingHelper.actionFilterTrips(new TripsFilterDataAnalyticsWrapper(tripFilterData, regions, parentActivities));
    }

    public void resetFilters() {
        dateFilterItem.reset();
        filterModel.reset();
        themeHeaderModel.setChecked(true);
        regionHeaderModel.setChecked(true);
        soldOutModel.setActive(false);
        favoriteModel.setActive(false);
        recentlyAddedModel.setActive(false);
        setRegionsChecked(true);
        setThemesChecked(true);
        view.dataSetChanged();
        //
        tripFilterData = TripsFilterData.createDefault(db);
        FilterBusEvent filterBusEvent = new FilterBusEvent(tripFilterData);
        eventBus.removeAllStickyEvents();
        eventBus.postSticky(filterBusEvent);
        TrackingHelper.actionFilterTrips(new TripsFilterDataAnalyticsWrapper(tripFilterData, regions, parentActivities));
    }

    private List<ActivityModel> getParentActivities() {
        return Queryable.from(activities).filter(input -> input.getParentId() == 0).toList();
    }

    private ArrayList<RegionModel> getAcceptedRegions() {
        ArrayList<RegionModel> regionsList = new ArrayList<>();
        if (regions != null) {
            Queryable.from(regions).filter(RegionModel::isChecked).forEachR(regionsList::add);
        }

        return regionsList;
    }

    private ArrayList<ActivityModel> getAcceptedThemes() {
        ArrayList<ActivityModel> themesList = new ArrayList<>();
        if (parentActivities != null) {
            themesList = new ArrayList<>();
            for (ActivityModel activity : parentActivities) {
                if (activity.isChecked()) {
                    themesList.addAll(Queryable.from(activities).filter((input) -> input.getParentId()
                            == activity.getId()).toList());
                    themesList.add(activity);
                }
            }
        }

        return themesList;
    }

    public void onEvent(RequestFilterDataEvent event) {
        acceptFilters();
    }

    public void onRangeBarDurationEvent(int minNights, int maxNights) {
        tripFilterData.setMinNights(minNights);
        tripFilterData.setMaxNights(maxNights);
    }

    public void onRangeBarPriceEvent(double minPrice, double maxPrice) {
        tripFilterData.setMinPrice(minPrice);
        tripFilterData.setMaxPrice(maxPrice);
    }

    public void onEvent(ToggleThemeVisibilityEvent event) {
        themeHeaderModel.setHide(!themeHeaderModel.isHide());
        fillData();
    }

    public void onEvent(ToggleRegionVisibilityEvent event) {
        regionHeaderModel.setHide(!regionHeaderModel.isHide());
        fillData();
    }

    public void onFilterShowSoldOutEvent(boolean isSoldOut) {
        tripFilterData.setShowSoldOut(isSoldOut);
    }

    public void onFilterShowFavoritesEvent(boolean isSoldOut) {
        tripFilterData.setShowFavorites(isSoldOut);
    }

    public void onFilterShowRecentlyAddedEvent(boolean enabled) {
        tripFilterData.setShowRecentlyAdded(enabled);
    }

    public void onCheckBoxAllRegionsPressedEvent(boolean isChecked) {
        setRegionsChecked(isChecked);
        tripFilterData.setAcceptedRegions(getAcceptedRegions());
        view.dataSetChanged();
    }

    public void onCheckBoxAllThemePressedEvent(boolean isChecked) {
        setThemesChecked(isChecked);
        tripFilterData.setAcceptedActivities(getAcceptedThemes());
        view.dataSetChanged();
    }

    public void onEvent(ResetFiltersEvent event) {
        resetFilters();
    }

    public void onThemeSetChangedEvent() {
        boolean allIsChecked = true;
        for (ActivityModel activity : parentActivities) {
            if (!activity.isChecked()) {
                allIsChecked = false;
                break;
            }
        }
        themeHeaderModel.setChecked(allIsChecked);
        tripFilterData.setAcceptedActivities(new ArrayList<>(Queryable.from(parentActivities).filter(ActivityModel::isChecked).toList()));
        view.dataSetChanged();
    }

    public void onRegionSetChangedEvent() {
        boolean allIsChecked = true;
        for (RegionModel region : regions) {
            if (!region.isChecked()) {
                allIsChecked = false;
                break;
            }
        }
        regionHeaderModel.setChecked(allIsChecked);
        tripFilterData.setAcceptedRegions(new ArrayList<>(Queryable.from(regions).filter(RegionModel::isChecked).toList()));
        view.dataSetChanged();
    }

    public void onDatesChanged(DateFilterItem item) {
        tripFilterData.setStartDate(item.getStartDate());
        tripFilterData.setEndDate(item.getEndDate());
    }

    public interface View extends Presenter.View {

        void dataSetChanged();

        BaseArrayListAdapter getAdapter();
    }
}