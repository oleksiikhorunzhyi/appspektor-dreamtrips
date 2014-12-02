package com.worldventures.dreamtrips.view.presentation.activity;

import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.view.presentation.adapter.TripPresentation;

import org.robobinding.annotation.ItemPresentationModel;
import org.robobinding.annotation.PresentationModel;
import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@PresentationModel
public class MainActivityPresentation implements HasPresentationModelChangeSupport {
    private static final String TRIPS = "trips";
    private PresentationModelChangeSupport changeSupport;

    private DataManager dataManager;
    private List<Trip> trips;
    private View view;

    public MainActivityPresentation(View view, DataManager dataManager) {
        this.view = view;
        this.dataManager = dataManager;
        this.changeSupport = new PresentationModelChangeSupport(this);
        loadTrips();
    }

    public void loadTrips() {
        dataManager.getTrips(new Callback<List<Trip>>() {
            @Override
            public void success(List<Trip> trips, Response response) {
                setTrips(trips);
                changeSupport.firePropertyChange(TRIPS);
                view.tripsLoaded();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @ItemPresentationModel(value = TripPresentation.class)
    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    @Override
    public PresentationModelChangeSupport getPresentationModelChangeSupport() {
        return changeSupport;
    }

    public static interface View {
        void tripsLoaded();
    }
}
