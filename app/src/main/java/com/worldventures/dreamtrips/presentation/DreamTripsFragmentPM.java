package com.worldventures.dreamtrips.presentation;

import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.LoaderFactory;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.Trip;

import org.robobinding.annotation.PresentationModel;

import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Edward on 19.01.15.
 * presentation model for fragment with list of the trips
 */
@PresentationModel
public class DreamTripsFragmentPM extends BasePresentation<DreamTripsFragmentPM.View> {

    @Inject
    DreamTripsApi dreamTripsApi;

    @Inject
    LoaderFactory loaderFactory;

    List<Trip> data;
    CollectionController<Trip> tripsController;

    public DreamTripsFragmentPM(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        this.tripsController = loaderFactory.create(0, (context, params) -> {
            this.data = this.loadTrips();
            return this.data;
        });

    }

    public CollectionController<Trip> getTripsController() {
        return tripsController;
    }

    public List<Trip> loadTrips() {
        return dreamTripsApi.getTrips();
    }

    public void onItemClick(int position) {
        activityRouter.openTripDetails(data.get(position));
    }


    public static interface View extends BasePresentation.View {
    }

}
