package com.worldventures.dreamtrips.presentation.tripimages;

import android.util.Log;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.core.api.spice.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.Inspiration;
import com.worldventures.dreamtrips.presentation.TripImagesListPM;

import java.util.ArrayList;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type;

public class InspireMePM extends TripImagesListPM<Inspiration> {
    public InspireMePM(View view) {
        super(view, Type.INSPIRE_ME);
    }

    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        return new TripImagesRoboSpiceController() {

            @Override
            public SpiceRequest<ArrayList<Inspiration>> getRefreshRequest() {
                return new DreamTripsRequest.GetInspireMePhotos(PER_PAGE, 1);
            }

            @Override
            public SpiceRequest<ArrayList<Inspiration>> getNextPageRequest(int currentCount) {
                Log.d("LoadNext", "count:" + currentCount + "; page: " + ((currentCount / PER_PAGE) + 1));
                return new DreamTripsRequest.GetInspireMePhotos(PER_PAGE, currentCount / PER_PAGE + 1);
            }
        };
    }
}