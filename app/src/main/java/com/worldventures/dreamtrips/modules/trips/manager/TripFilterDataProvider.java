package com.worldventures.dreamtrips.modules.trips.manager;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.util.TripsFilterData;

import de.greenrobot.event.EventBus;

public class TripFilterDataProvider {

    EventBus eventBus;
    SnappyRepository db;

    public TripFilterDataProvider(EventBus eventBus, SnappyRepository db) {
        this.eventBus = eventBus;
        this.db = db;
    }

    public TripsFilterData get() {
        FilterBusEvent filterBusEvent = eventBus.getStickyEvent(FilterBusEvent.class);
        TripsFilterData tripsFilterData;
        if (filterBusEvent == null || filterBusEvent.getTripsFilterData() == null) {
            tripsFilterData = TripsFilterData.createDefault(db);
        } else {
            tripsFilterData = filterBusEvent.getTripsFilterData();
        }

        return tripsFilterData;
    }
}
