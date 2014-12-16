package com.worldventures.dreamtrips.view.presentation;

import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.view.activity.Injector;
import com.worldventures.dreamtrips.view.presentation.BasePresentation;
import com.worldventures.dreamtrips.view.presentation.adapter.TripPresentation;

import org.robobinding.annotation.ItemPresentationModel;
import org.robobinding.annotation.PresentationModel;
import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.util.ArrayList;
import java.util.List;

@PresentationModel
public class MainActivityPresentation extends BasePresentation implements HasPresentationModelChangeSupport {
    private static final String TRIPS = "trips";
    private PresentationModelChangeSupport changeSupport;

    private List<Trip> trips = new ArrayList<>();
    private View view;


    public MainActivityPresentation(View view, Injector graf) {
        super(graf);
        this.changeSupport = new PresentationModelChangeSupport(this);
        this.view = view;
        loadTrips();
    }

    public void loadTrips() {
        dataManager.getTrips((trips, e) -> {
            setTrips(trips);
            changeSupport.firePropertyChange(TRIPS);
            view.tripsLoaded();
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
