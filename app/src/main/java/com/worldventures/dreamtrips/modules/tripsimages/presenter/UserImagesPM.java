package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.core.api.request.photos.GetUserPhotos;
import com.worldventures.dreamtrips.core.model.Photo;

import java.util.ArrayList;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public class UserImagesPM extends TripImagesListPM<Photo> {
    public UserImagesPM(View view) {
        super(view, Type.MEMBER_IMAGES);
    }

    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        return new TripImagesRoboSpiceController() {
            @Override
            public SpiceRequest<ArrayList<Photo>> getRefreshRequest() {
                return new GetUserPhotos(PER_PAGE, 1);
            }

            @Override
            public SpiceRequest<ArrayList<Photo>> getNextPageRequest(int currentCount) {
                return new GetUserPhotos(PER_PAGE, currentCount / PER_PAGE + 1);
            }
        };
    }

}