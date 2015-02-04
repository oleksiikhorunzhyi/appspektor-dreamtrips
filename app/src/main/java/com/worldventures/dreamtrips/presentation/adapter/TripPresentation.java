package com.worldventures.dreamtrips.presentation.adapter;

import com.worldventures.dreamtrips.core.model.Trip;

import org.robobinding.itempresentationmodel.ItemContext;
import org.robobinding.itempresentationmodel.ItemPresentationModel;

public class TripPresentation implements ItemPresentationModel<Trip> {
    private Trip trip;

    @Override
    public void updateData(Trip trip, ItemContext itemContext) {
        this.trip = trip;
    }

    public String getName() {
        return trip.getName();
    }

    public String getDescription() {
        return trip.getDescription();
    }
}
