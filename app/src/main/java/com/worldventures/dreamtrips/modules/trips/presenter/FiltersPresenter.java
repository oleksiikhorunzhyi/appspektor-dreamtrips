package com.worldventures.dreamtrips.modules.trips.presenter;

import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.CheckBoxAllRegionsPressedEvent;
import com.worldventures.dreamtrips.core.utils.events.CheckBoxAllThemePressedEvent;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.core.utils.events.RangeBarDurationEvent;
import com.worldventures.dreamtrips.core.utils.events.RangeBarPriceEvent;
import com.worldventures.dreamtrips.core.utils.events.RegionSetChangedEvent;
import com.worldventures.dreamtrips.core.utils.events.RequestFilterDataEvent;
import com.worldventures.dreamtrips.core.utils.events.ResetFiltersEvent;
import com.worldventures.dreamtrips.core.utils.events.ThemeSetChangedEvent;
import com.worldventures.dreamtrips.core.utils.events.ToggleRegionVisibilityEvent;
import com.worldventures.dreamtrips.core.utils.events.ToggleThemeVisibilityEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.event.FilterShowFavoritesEvent;
import com.worldventures.dreamtrips.modules.trips.event.FilterShowRecentlyAddedEvent;
import com.worldventures.dreamtrips.modules.trips.event.FilterShowSoldOutEvent;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.DateFilterItem;
import com.worldventures.dreamtrips.modules.trips.model.FilterFavoriteModel;
import com.worldventures.dreamtrips.modules.trips.model.FilterModel;
import com.worldventures.dreamtrips.modules.trips.model.FilterRecentlyAddedModel;
import com.worldventures.dreamtrips.modules.trips.model.FilterSoldOutModel;
import com.worldventures.dreamtrips.modules.trips.model.RegionHeaderModel;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;
import com.worldventures.dreamtrips.modules.trips.model.ThemeHeaderModel;

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
    double maxPrice = Double.MAX_VALUE;
    @State
    double minPrice = 0.0d;
    @State
    int maxNights = Integer.MAX_VALUE;
    @State
    int minNights = 0;
    @State
    boolean showSoldOut = false;
    @State
    boolean showFavorites = false;
    @State
    boolean showRecentlyAdded = false;
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
        FilterBusEvent filterBusEvent = new FilterBusEvent();
        filterBusEvent.setMaxNights(maxNights);
        filterBusEvent.setMinPrice(minPrice);
        filterBusEvent.setMaxPrice(maxPrice);
        filterBusEvent.setMinNights(minNights);
        filterBusEvent.setAcceptedRegions(getAcceptedRegions());
        filterBusEvent.setAcceptedActivities(getAcceptedThemes());
        filterBusEvent.setShowSoldOut(showSoldOut);
        filterBusEvent.setShowFavorites(showFavorites);
        filterBusEvent.setDateFilterItem(dateFilterItem);
        filterBusEvent.setShowRecentlyAdded(showRecentlyAdded);
        eventBus.removeStickyEvent(FilterBusEvent.class);
        eventBus.postSticky(filterBusEvent);
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

        FilterBusEvent filterBusEvent = new FilterBusEvent();
        filterBusEvent.setReset(true);

        eventBus.removeAllStickyEvents();
        eventBus.postSticky(filterBusEvent);
    }

    private List<ActivityModel> getParentActivities() {
        return Queryable.from(activities).filter(input -> input.getParentId() == 0).toList();
    }

    private ArrayList<Integer> getAcceptedRegions() {
        if (regionHeaderModel.isChecked()) {
            return null;
        }

        ArrayList<Integer> regionsList = null;

        if (regions != null) {
            regionsList = new ArrayList<>();
            for (RegionModel region : regions) {
                if (region.isChecked()) {
                    regionsList.add(region.getId());
                }
            }
        }

        return regionsList;
    }

    private ArrayList<ActivityModel> getAcceptedThemes() {
        if (themeHeaderModel.isChecked()) {
            return null;
        }

        ArrayList<ActivityModel> themesList = null;
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

    public void onEvent(RangeBarDurationEvent event) {
        this.minNights = event.getMinNights();
        this.maxNights = event.getMaxNights();
    }

    public void onEvent(RangeBarPriceEvent event) {
        this.minPrice = event.getMinPrice();
        this.maxPrice = event.getMaxPrice();
    }

    public void onEvent(ToggleThemeVisibilityEvent event) {
        themeHeaderModel.setHide(!themeHeaderModel.isHide());
        fillData();
    }

    public void onEvent(ToggleRegionVisibilityEvent event) {
        regionHeaderModel.setHide(!regionHeaderModel.isHide());
        fillData();
    }

    public void onEvent(FilterShowSoldOutEvent soldOutEvent) {
        showSoldOut = soldOutEvent.isSoldOut();
    }

    public void onEvent(FilterShowFavoritesEvent soldOutEvent) {
        showFavorites = soldOutEvent.isShowFavorites();
    }

    public void onEvent(FilterShowRecentlyAddedEvent addedEvent) {
        showRecentlyAdded = addedEvent.isShowRecentlyAdded();
    }

    public void onEvent(CheckBoxAllRegionsPressedEvent event) {
        setRegionsChecked(event.isChecked());
        view.dataSetChanged();
    }

    public void onEvent(CheckBoxAllThemePressedEvent event) {
        setThemesChecked(event.isChecked());
        view.dataSetChanged();
    }

    public void onEvent(ResetFiltersEvent event) {
        resetFilters();
    }

    public void onEvent(ThemeSetChangedEvent event) {
        boolean allIsChecked = true;
        for (ActivityModel activity : parentActivities) {
            if (!activity.isChecked()) {
                allIsChecked = false;
                break;
            }
        }
        themeHeaderModel.setChecked(allIsChecked);
        view.dataSetChanged();
    }

    public void onEvent(RegionSetChangedEvent event) {
        boolean allIsChecked = true;
        for (RegionModel region : regions) {
            if (!region.isChecked()) {
                allIsChecked = false;
                break;
            }
        }
        regionHeaderModel.setChecked(allIsChecked);
        view.dataSetChanged();
    }


    public interface View extends Presenter.View {
        void dataSetChanged();

        BaseArrayListAdapter getAdapter();
    }

}