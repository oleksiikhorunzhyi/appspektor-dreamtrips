package com.worldventures.dreamtrips.modules.trips.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.CheckBoxAllPressedEvent;
import com.worldventures.dreamtrips.core.utils.events.CheckBoxAllThemePressedEvent;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.core.utils.events.RangeBarDurationEvent;
import com.worldventures.dreamtrips.core.utils.events.RangeBarPriceEvent;
import com.worldventures.dreamtrips.core.utils.events.RegionSetChangedEvent;
import com.worldventures.dreamtrips.core.utils.events.RequestFilterDataEvent;
import com.worldventures.dreamtrips.core.utils.events.ResetFiltersEvent;
import com.worldventures.dreamtrips.core.utils.events.SoldOutEvent;
import com.worldventures.dreamtrips.core.utils.events.ThemeSetChangedEvent;
import com.worldventures.dreamtrips.core.utils.events.ToggleRegionVisibilityEvent;
import com.worldventures.dreamtrips.core.utils.events.ToggleThemeVisibilityEvent;
import com.worldventures.dreamtrips.core.utils.events.UpdateRegionsAndThemesEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.api.GetActivitiesQuery;
import com.worldventures.dreamtrips.modules.trips.api.GetRegionsQuery;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.DateFilterItem;
import com.worldventures.dreamtrips.modules.trips.model.FilterModel;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;
import com.worldventures.dreamtrips.modules.trips.model.SoldOutModel;
import com.worldventures.dreamtrips.modules.trips.model.ThemeHeaderModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FiltersPresenter extends Presenter<FiltersPresenter.View> {

    @Inject
    protected SnappyRepository db;

    private List<RegionModel> regions;
    private List<ActivityModel> activities;
    private List<ActivityModel> parentActivities;

    /**
     * variables for filtering
     */
    private double maxPrice = Double.MAX_VALUE;
    private double minPrice = 0.0d;
    private int maxNights = Integer.MAX_VALUE;
    private int minNights = 0;
    private boolean showSoldOut = false;
    private FilterModel filterModel;
    private ThemeHeaderModel themeHeaderModel;
    private SoldOutModel soldOutModel;
    private DateFilterItem dateFilterItem;

    public FiltersPresenter(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        filterModel = new FilterModel();
        dateFilterItem = new DateFilterItem();
        dateFilterItem.reset();
        themeHeaderModel = new ThemeHeaderModel();
        soldOutModel = new SoldOutModel();
    }

    public void onEvent(UpdateRegionsAndThemesEvent event) {
        loadFilters(true);
        eventBus.cancelEventDelivery(event);
    }

    public void loadFilters(boolean fromApi) {
        view.startLoading();

        dreamSpiceManager.execute(new GetActivitiesQuery(db, fromApi), new RequestListener<ArrayList<ActivityModel>>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                //nothing to do here
            }

            @Override
            public void onRequestSuccess(ArrayList<ActivityModel> activities) {
                FiltersPresenter.this.activities = activities;
                parentActivities = getParentActivities();
                if (regions != null && !regions.isEmpty()) {
                    fillData();
                }
            }
        });

        dreamSpiceManager.execute(new GetRegionsQuery(db, fromApi), new RequestListener<ArrayList<RegionModel>>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                //nothing to do here
            }

            @Override
            public void onRequestSuccess(ArrayList<RegionModel> regions) {
                FiltersPresenter.this.regions = regions;
                if (activities != null && !activities.isEmpty()) {
                    fillData();
                }

            }
        });
    }

    public void fillData() {
        view.finishLoading();
        if (regions != null && activities != null) {

            List<Object> data = new ArrayList<>();
            data.clear();
            data.add(dateFilterItem);
            data.add(filterModel);
            if (!filterModel.isHide()) {
                data.addAll(regions);
            }

            data.add(themeHeaderModel);

            if (!themeHeaderModel.isHide()) {
                data.addAll(parentActivities);
            }

            setRegionsChecked(filterModel.isChecked());
            setThemesChecked(themeHeaderModel.isChecked());

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
        filterBusEvent.setDateFilterItem(dateFilterItem);

        eventBus.removeAllStickyEvents();
        eventBus.postSticky(filterBusEvent);
    }

    public void resetFilters() {
        dateFilterItem.reset();
        filterModel.reset();
        themeHeaderModel.setChecked(true);
        soldOutModel.setShowSoldOut(false);
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

    private List<Integer> getAcceptedRegions() {
        List<Integer> regionsList = null;

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

    private List<ActivityModel> getAcceptedThemes() {
        if (themeHeaderModel.isChecked()) {
            return null;
        }

        List<ActivityModel> themesList = null;
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

    private boolean needUpdate() {
        return db.isEmpty(SnappyRepository.REGIONS) && db.isEmpty(SnappyRepository.ACTIVITIES);
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
        filterModel.setHide(!filterModel.isHide());
        fillData();
    }

    public void onEvent(SoldOutEvent soldOutEvent) {
        showSoldOut = soldOutEvent.isSoldOut();
    }

    public void onEvent(CheckBoxAllPressedEvent event) {
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
        filterModel.setChecked(allIsChecked);
        view.dataSetChanged();
    }


    public static interface View extends Presenter.View {
        void dataSetChanged();

        void startLoading();

        void finishLoading();

        BaseArrayListAdapter getAdapter();
    }

}