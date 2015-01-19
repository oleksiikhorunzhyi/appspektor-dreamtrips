package com.worldventures.dreamtrips.presentation;

import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.core.model.response.ListTripResponse;

import org.robobinding.annotation.PresentationModel;

import java.util.ArrayList;
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

    final Callback<ListTripResponse> callback = new Callback<ListTripResponse>() {
        @Override
        public void success(ListTripResponse listPhotoResponse, Response response) {
            view.clearAdapter();
            data = listPhotoResponse.getData();
            view.setTrips(data);
        }

        @Override
        public void failure(RetrofitError error) {
            view.setTrips(null);
            handleError(error);
        }
    };


    public void loadTrips() {
        //TODO implement api call
        view.clearAdapter();
        data = new ArrayList<>();
        data.add(Trip.dummy());
        data.add(Trip.dummy());
        data.add(Trip.dummy());
        view.setTrips(data);
    }

    public void onItemClick(int position) {
        activityRouter.openTripDetails(data.get(position));
    }


    public static interface View extends BasePresentation.View {
        void setTrips(List<Trip> photos);

        void clearAdapter();
    }


}
