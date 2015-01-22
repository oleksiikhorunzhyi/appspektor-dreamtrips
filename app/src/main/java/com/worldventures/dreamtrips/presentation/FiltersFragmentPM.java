package com.worldventures.dreamtrips.presentation;

import com.google.gson.reflect.TypeToken;
import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.LoaderFactory;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.FilterModel;
import com.worldventures.dreamtrips.core.model.Region;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.utils.FileUtils;
import com.worldventures.dreamtrips.utils.busevents.CheckBoxAllPressedEvent;
import com.worldventures.dreamtrips.utils.busevents.FilterBusEvent;
import com.worldventures.dreamtrips.utils.busevents.RangeBarDurationEvent;
import com.worldventures.dreamtrips.utils.busevents.RangeBarPriceEvent;
import com.worldventures.dreamtrips.utils.busevents.RegionSetChangedEvent;

import org.robobinding.annotation.PresentationModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by 1 on 22.01.15.
 */
@PresentationModel
public class FiltersFragmentPM extends BasePresentation<FiltersFragmentPM.View> {

    @Inject
    DreamTripsApi dreamTripsApi;

    @Inject
    LoaderFactory loaderFactory;

    @Inject
    Prefs prefs;

    @Inject
    @Global
    EventBus eventBus;

    List<Object> data = new ArrayList<>();
    List<Region> regions;
    FilterModel filterModel;

    CollectionController<Object> regionController;

    private double maxPrice = Double.MAX_VALUE;
    private double minPrice = 0.0d;
    private int maxNights = Integer.MAX_VALUE;
    private int minNights = 0;


    public FiltersFragmentPM(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        filterModel = new FilterModel();
        this.regionController = loaderFactory.create(0, (context, params) -> {
            if (needUpdate()) {
                this.regions = this.loadRegion();

                if (regions != null && regions.size() > 0) {
                    FileUtils.saveJsonToCache(context, this.regions, FileUtils.REGIONS);
                    prefs.put(Prefs.REGIONS_LOADED, true);
                }
            } else {
                this.regions = FileUtils.parseJsonFromCache(context, new TypeToken<List<Region>>() {
                }.getType(), FileUtils.REGIONS);
            }

            this.data.clear();
            this.data.add(filterModel);
            this.data.addAll(regions);
            return data;
        });
        eventBus.register(this);
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

    public void setChecked(boolean isChecked) {
        if (regions != null) {
            for (Region region : regions) {
                region.setChecked(isChecked);
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
        eventBus.post(filterBusEvent);
    }

    public void resetFilters() {
        FilterBusEvent filterBusEvent = new FilterBusEvent();
        filterBusEvent.setReset(true);
        eventBus.post(filterBusEvent);
    }

    private List<Integer> getAcceptedRegions() {
        List<Integer> regionsList = new ArrayList<>();

        for (Region region : regions) {
            if (region.isChecked()) {
                regionsList.add(region.getId());
            }
        }

        return regionsList;
    }

    public CollectionController<Object> getRegionController() {
        return regionController;
    }

    private boolean needUpdate() {
        return !prefs.getBoolean(Prefs.REGIONS_LOADED);
    }

    public List<Region> loadRegion() {
        return dreamTripsApi.getRegions();
    }

    public void onEvent(RangeBarDurationEvent event) {
        this.minNights = event.getMinNights();
        this.maxNights = event.getMaxNights();
    }

    public void onEvent(RangeBarPriceEvent event) {
        this.minPrice = event.getMinPrice();
        this.maxPrice = event.getMaxPrice();
    }

    public void onEvent(CheckBoxAllPressedEvent event) {
        setChecked(event.isChecked());
        view.dataSetChanged();
    }

    public static interface View extends BasePresentation.View {
        void dataSetChanged();
    }

}
