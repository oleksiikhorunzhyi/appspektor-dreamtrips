package com.worldventures.dreamtrips.presentation.tripimages;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.core.api.spice.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.presentation.TripImagesListPM;

import java.util.ArrayList;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type;

public class UserImagesPM extends TripImagesListPM<Photo> {
    public UserImagesPM(View view) {
        super(view, Type.MEMBER_IMAGES);
    }

    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        return new TripImagesRoboSpiceController() {
            @Override
            public SpiceRequest<ArrayList<Photo>> getRefreshRequest() {
                return new DreamTripsRequest.GetUserPhotos(PER_PAGE, 1);
            }

            @Override
            public SpiceRequest<ArrayList<Photo>> getNextPageRequest(int currentCount) {
                return new DreamTripsRequest.GetUserPhotos(PER_PAGE, currentCount / PER_PAGE + 1);
            }
        };
    }

}
