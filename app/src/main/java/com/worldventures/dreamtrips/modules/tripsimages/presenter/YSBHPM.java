package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetYSBHPhotos;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public class YSBHPM extends TripImagesListPM<Photo> {
    public YSBHPM(View view) {
        super(view, Type.YOU_SHOULD_BE_HERE);
    }

    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        return new TripImagesRoboSpiceController() {
            @Override
            public SpiceRequest<ArrayList<Photo>> getRefreshRequest() {
                return new GetYSBHPhotos(PER_PAGE, 1);
            }

            @Override
            public SpiceRequest<ArrayList<Photo>> getNextPageRequest(int currentCount) {
                return new GetYSBHPhotos(PER_PAGE, currentCount / PER_PAGE + 1);
            }
        };
    }
}