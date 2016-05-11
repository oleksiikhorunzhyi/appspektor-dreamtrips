package com.worldventures.dreamtrips.modules.trips.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.model.TripQueryData;

import java.util.ArrayList;

public class GetTripsQuery extends DreamTripsRequest<ArrayList<TripModel>> {


    private TripQueryData tripQueryData;


    @Override
    public ArrayList<TripModel> loadDataFromNetwork() throws Exception {
        return getService().getTripsPaginated(
                tripQueryData.page(),
                tripQueryData.perPage(),
                tripQueryData.query(),
                tripQueryData.durationMin(),
                tripQueryData.durationMax(),
                tripQueryData.priceMin(),
                tripQueryData.priceMax(),
                tripQueryData.startDate(),
                tripQueryData.endDate(),
                tripQueryData.regions(),
                tripQueryData.activities(),
                tripQueryData.soldOut(),
                tripQueryData.recent(),
                tripQueryData.liked()
        );
    }


    @Override
    public int getErrorMessage() {
        return R.string.string_failed_to_load_trips;
    }


    public GetTripsQuery(TripQueryData tripQueryData) {
        super((Class<ArrayList<TripModel>>) new ArrayList<TripModel>().getClass());
        this.tripQueryData = tripQueryData;
    }


}
