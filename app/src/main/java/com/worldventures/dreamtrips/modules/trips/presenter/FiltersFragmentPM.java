package com.worldventures.dreamtrips.modules.trips.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.modules.trips.api.GetActivitiesRequest;
import com.worldventures.dreamtrips.modules.trips.api.GetRegionsRequest;
import com.worldventures.dreamtrips.modules.trips.model.Activity;
import com.worldventures.dreamtrips.modules.trips.model.DateFilterItem;
import com.worldventures.dreamtrips.modules.trips.model.FilterModel;
import com.worldventures.dreamtrips.modules.trips.model.Region;
import com.worldventures.dreamtrips.modules.trips.model.SoldOutModel;
import com.worldventures.dreamtrips.modules.trips.model.ThemeHeaderModel;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by 1 on 22.01.15.
 */
public class FiltersFragmentPM extends BasePresenter<FiltersFragmentPM.View> {

    @Inject
    SnappyRepository db;

    @Inject
    @Global
    EventBus eventBus;

    // private List<Object> data = new ArrayList<>();
    private List<Region> regions;
    private List<Activity> activities;
    private List<Activity> parentActivities;

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

    public FiltersFragmentPM(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        filterModel = new FilterModel();
        dateFilterItem = new DateFilterItem();
        themeHeaderModel = new ThemeHeaderModel();
        soldOutModel = new SoldOutModel();
        eventBus.register(this);
    }

    public void loadFilters() {
        view.startLoading();
        dreamSpiceManager.execute(new GetActivitiesRequest(db), new RequestListener<ArrayList<Activity>>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(ArrayList<Activity> activities) {
                FiltersFragmentPM.this.activities = activities;
                parentActivities = getParentActivities();
                if (regions != null && regions.size() != 0) {
                    fillData();
                }
            }
        });
        dreamSpiceManager.execute(new GetRegionsRequest(db), new RequestListener<ArrayList<Region>>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(ArrayList<Region> regions) {
                FiltersFragmentPM.this.regions = regions;
                if (activities != null && activities.size() != 0) {
                    fillData();
                }

            }
        });
    }

    public void fillData() {
        if (regions != null && activities != null) {
            List<Object> data = new ArrayList<>();
            view.finishLoading();
            data.clear();
            data.add(dateFilterItem);
            data.add(filterModel);
            if (!filterModel.isHide())
                data.addAll(regions);
            data.add(themeHeaderModel);
            if (!themeHeaderModel.isHide())
                data.addAll(parentActivities);

            setRegionsChecked(filterModel.isChecked());
            setThemesChecked(themeHeaderModel.isChecked());

            view.getAdapter().clear();
            view.getAdapter().addItems(data);
        }
    }

    public void setRegionsChecked(boolean isChecked) {
        if (regions != null) {
            for (Region region : regions) {
                region.setChecked(isChecked);
            }
        }
    }

    public void setThemesChecked(boolean isChecked) {
        if (parentActivities != null) {
            for (Activity activity : parentActivities) {
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

    private List<Activity> getParentActivities() {
        List<Activity> parentActivities = new ArrayList<>();
        parentActivities.addAll(Queryable.from(activities).filter((input) -> input.getParent_id() == 0).toList());
        return parentActivities;
    }

    private List<Integer> getAcceptedRegions() {
        List<Integer> regionsList = null;

        if (regions != null) {
            regionsList = new ArrayList<>();
            for (Region region : regions) {
                if (region.isChecked()) {
                    regionsList.add(region.getId());
                }
            }
        }

        return regionsList;
    }

    private List<Activity> getAcceptedThemes() {
        List<Activity> themesList = null;
        if (parentActivities != null) {
            themesList = new ArrayList<>();
            for (Activity activity : parentActivities) {
                if (activity.isChecked()) {
                    themesList.addAll(Queryable.from(activities).filter((input) -> input.getParent_id()
                            == activity.getId()).toList());
                }
            }
        }

        return themesList;
    }

    private boolean needUpdate() throws ExecutionException, InterruptedException {
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
        for (Activity activity : parentActivities) {
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
        for (Region region : regions) {
            if (!region.isChecked()) {
                allIsChecked = false;
                break;
            }
        }
        filterModel.setChecked(allIsChecked);
        view.dataSetChanged();
    }


    public static interface View extends BasePresenter.View {
        void dataSetChanged();

        void startLoading();

        void finishLoading();

        BaseArrayListAdapter getAdapter();
    }

}