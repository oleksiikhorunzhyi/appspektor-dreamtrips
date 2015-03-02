package com.worldventures.dreamtrips.presentation;

import com.google.common.collect.Collections2;
import com.google.gson.reflect.TypeToken;
import com.snappydb.DB;
import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.LoaderFactory;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.Activity;
import com.worldventures.dreamtrips.core.model.DateFilterItem;
import com.worldventures.dreamtrips.core.model.FilterModel;
import com.worldventures.dreamtrips.core.model.Region;
import com.worldventures.dreamtrips.core.model.SoldOutModel;
import com.worldventures.dreamtrips.core.model.ThemeHeaderModel;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.utils.FileUtils;
import com.worldventures.dreamtrips.utils.SnappyUtils;
import com.worldventures.dreamtrips.utils.busevents.CheckBoxAllPressedEvent;
import com.worldventures.dreamtrips.utils.busevents.CheckBoxAllThemePressedEvent;
import com.worldventures.dreamtrips.utils.busevents.FilterBusEvent;
import com.worldventures.dreamtrips.utils.busevents.RangeBarDurationEvent;
import com.worldventures.dreamtrips.utils.busevents.RangeBarPriceEvent;
import com.worldventures.dreamtrips.utils.busevents.RegionSetChangedEvent;
import com.worldventures.dreamtrips.utils.busevents.RequestFilterDataEvent;
import com.worldventures.dreamtrips.utils.busevents.ResetFiltersEvent;
import com.worldventures.dreamtrips.utils.busevents.SoldOutEvent;
import com.worldventures.dreamtrips.utils.busevents.ThemeSetChangedEvent;
import com.worldventures.dreamtrips.utils.busevents.ToggleRegionVisibilityEvent;
import com.worldventures.dreamtrips.utils.busevents.ToggleThemeVisibilityEvent;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by 1 on 22.01.15.
 */
public class FiltersFragmentPM extends BasePresentation<FiltersFragmentPM.View> {

    @Inject
    DreamTripsApi dreamTripsApi;

    @Inject
    LoaderFactory loaderFactory;

    @Inject
    Prefs prefs;

    @Inject
    SnappyRepository db;

    @Inject
    @Global
    EventBus eventBus;

    private List<Object> data = new ArrayList<>();
    private List<Region> regions = new ArrayList<>();
    private List<Activity> activities = new ArrayList<>();
    private List<Activity> parentActivities;


    private CollectionController<Object> regionController;

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
        this.regionController = loaderFactory.create(0, (context, params) -> {
            try {
                if (needUpdate()) {
                    this.regions = this.loadRegion();
                    this.activities = this.dreamTripsApi.getActivities();

                    if (activities != null && activities.size() > 0) {
                        db.putList(this.activities, SnappyRepository.ACTIVITIES, Activity.class);
                        //FileUtils.saveJsonToCache(context, this.activities, FileUtils.ACTIVITIES);
                        prefs.put(Prefs.ACTIVITIES_LOADED, true);
                    }

                    if (regions != null && regions.size() > 0) {
                        db.putList(this.regions, SnappyRepository.REGIONS, Region.class);
                        //   FileUtils.saveJsonToCache(context, this.regions, FileUtils.REGIONS);
                        prefs.put(Prefs.REGIONS_LOADED, true);
                    }
                } else {
                    this.regions = db.readList(SnappyRepository.REGIONS, Region.class);
                    this.activities = db.readList(SnappyRepository.ACTIVITIES, Activity.class);

                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            parentActivities = getParentActivities();
            fillData();
            return data;
        });

        eventBus.register(this);
    }

    private void fillData() {
        this.data.clear();
        this.data.add(dateFilterItem);
        this.data.add(filterModel);
        if (!filterModel.isHide())
            this.data.addAll(regions);
        this.data.add(themeHeaderModel);
        if (!themeHeaderModel.isHide())
            this.data.addAll(parentActivities);

        setRegionsChecked(filterModel.isChecked());
        setThemesChecked(themeHeaderModel.isChecked());

        //this.data.add(soldOutModel);
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
        parentActivities.addAll(Collections2.filter(activities, (input) -> input.getParent_id() == 0));
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
                    themesList.addAll(Collections2.filter(activities, (input) -> input.getParent_id()
                            == activity.getId()));
                }
            }
        }

        return themesList;
    }

    public CollectionController<Object> getRegionController() {
        return regionController;
    }

    private boolean needUpdate() throws ExecutionException, InterruptedException {
        return db.isEmpty(SnappyRepository.REGIONS) && db.isEmpty(SnappyRepository.ACTIVITIES);
    }

    public List<Region> loadRegion() {
        return dreamTripsApi.getRegions();
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
        getRegionController().reload();
    }

    public void onEvent(ToggleRegionVisibilityEvent event) {
        filterModel.setHide(!filterModel.isHide());
        getRegionController().reload();
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


    public static interface View extends BasePresentation.View {
        void dataSetChanged();
    }

}