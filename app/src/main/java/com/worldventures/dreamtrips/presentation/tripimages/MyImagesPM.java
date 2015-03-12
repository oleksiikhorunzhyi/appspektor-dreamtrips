package com.worldventures.dreamtrips.presentation.tripimages;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.core.api.spice.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.presentation.TripImagesListPM;

import java.util.ArrayList;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type;

public class MyImagesPM extends TripImagesListPM<IFullScreenAvailableObject> {
    public MyImagesPM(View view) {
        super(view, Type.MY_IMAGES);
    }


    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        User user = appSessionHolder.get().get().getUser();

        return new TripImagesRoboSpiceController() {
            @Override
            public SpiceRequest<ArrayList<IFullScreenAvailableObject>> getRefreshRequest() {
                DreamTripsRequest.GetMyPhotos getMyPhotos = new DreamTripsRequest.GetMyPhotos(user.getId(), PER_PAGE, 1);
                view.inject(getMyPhotos);
                return getMyPhotos;
            }

            @Override
            public SpiceRequest<ArrayList<IFullScreenAvailableObject>> getNextPageRequest(int currentCount) {
                DreamTripsRequest.GetMyPhotos getMyPhotos = new DreamTripsRequest.GetMyPhotos(user.getId(), PER_PAGE, currentCount / PER_PAGE + 1);
                view.inject(getMyPhotos);
                return getMyPhotos;
            }
        };
    }
}