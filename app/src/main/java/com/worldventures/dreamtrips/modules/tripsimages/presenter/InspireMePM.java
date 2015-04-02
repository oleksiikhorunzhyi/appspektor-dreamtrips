package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.util.Log;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetInspireMePhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;

import java.util.ArrayList;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public class InspireMePM extends TripImagesListPM<Inspiration> {
    public InspireMePM(View view) {
        super(view, Type.INSPIRE_ME);
    }

    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        return new TripImagesRoboSpiceController() {

            @Override
            public SpiceRequest<ArrayList<Inspiration>> getRefreshRequest() {
                return new GetInspireMePhotosQuery(PER_PAGE, 1);
            }

            @Override
            public SpiceRequest<ArrayList<Inspiration>> getNextPageRequest(int currentCount) {
                Log.d("LoadNext", "count:" + currentCount + "; page: " + ((currentCount / PER_PAGE) + 1));
                return new GetInspireMePhotosQuery(PER_PAGE, currentCount / PER_PAGE + 1);
            }
        };
    }
}