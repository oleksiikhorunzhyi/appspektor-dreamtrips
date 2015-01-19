package com.worldventures.dreamtrips.presentation;

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

    List<Trip> data;

    public DreamTripsFragmentPM(View view) {
        super(view);
    }

    final Callback<List<Trip>> callback = new Callback<List<Trip>>() {
        @Override
        public void success(List<Trip> listPhotoResponse, Response response) {
            view.clearAdapter();
            data = listPhotoResponse;
            view.setTrips(data);
        }

        @Override
        public void failure(RetrofitError error) {
            view.setTrips(null);
            handleError(error);
        }
    };


    public void loadTrips() {
        dreamTripsApi.getTrips(callback);
    }

    public void onItemClick(int position) {
        activityRouter.openTripDetails(data.get(position));
    }


    public static interface View extends BasePresentation.View {
        void setTrips(List<Trip> photos);

        void clearAdapter();
    }


}
