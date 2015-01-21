package com.worldventures.dreamtrips.presentation;

import com.google.common.collect.Collections2;
import com.google.gson.reflect.TypeToken;
import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.LoaderFactory;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.utils.FileUtils;

import org.robobinding.annotation.PresentationModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Edward on 19.01.15.
 * presentation model for fragment with list of the trips
 */
@PresentationModel
public class DreamTripsFragmentPM extends BasePresentation<DreamTripsFragmentPM.View> {

    private static final long DELTA = 10 * 60 * 1000;

    @Inject
    DreamTripsApi dreamTripsApi;

    @Inject
    LoaderFactory loaderFactory;

    @Inject
    Prefs prefs;

    List<Trip> data;
    CollectionController<Trip> tripsController;

    private boolean loadFromApi;
    private double maxPrice = Double.MAX_VALUE;
    private int maxNights = Integer.MAX_VALUE;

    public DreamTripsFragmentPM(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        this.tripsController = loaderFactory.create(0, (context, params) -> {
            if (needUpdate() || loadFromApi) {
                this.loadFromApi = false;
                this.data = this.loadTrips();

                FileUtils.saveJsonToCache(context, this.data);

                prefs.put(Prefs.LAST_SYNC, Calendar.getInstance().getTimeInMillis());
            } else {
                this.data = FileUtils.parseJsonFromCache(context, new TypeToken<List<Trip>>() {
                }.getType());
            }

            return performFiltering(data);
        });
    }

    public CollectionController<Trip> getTripsController() {
        return tripsController;
    }

    public void reload() {
        loadFromApi = true;
        tripsController.reload();
    }

    private List<Trip> performFiltering(List<Trip> data) {
        List<Trip> filteredTrips = new ArrayList<>();
        filteredTrips.addAll(Collections2.filter(data, (input) -> input.getPrice().getAmount() <= maxPrice
                && input.getDuration() <= maxNights));
        return filteredTrips;
    }

    public void resetFilters() {
        this.maxNights = Integer.MAX_VALUE;
        this.maxPrice = Double.MAX_VALUE;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public void setMaxNights(int maxNights) {
        this.maxNights = maxNights;
    }

    public List<Trip> loadTrips() {
        return dreamTripsApi.getTrips();
    }

    public void onItemClick(int position) {
        activityRouter.openTripDetails(data.get(position));
    }

    public boolean needUpdate() {
        long current = Calendar.getInstance().getTimeInMillis();
        return current - prefs.getLong(Prefs.LAST_SYNC) > DELTA;
    }

    public static interface View extends BasePresentation.View {
    }

}
