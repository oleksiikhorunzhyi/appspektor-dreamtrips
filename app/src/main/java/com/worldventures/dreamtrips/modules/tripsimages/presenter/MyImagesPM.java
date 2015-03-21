package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetMyPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

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
                GetMyPhotosQuery getMyPhotosQuery = new GetMyPhotosQuery(user.getId(), PER_PAGE, 1);
                view.inject(getMyPhotosQuery);
                return getMyPhotosQuery;
            }

            @Override
            public SpiceRequest<ArrayList<IFullScreenAvailableObject>> getNextPageRequest(int currentCount) {
                GetMyPhotosQuery getMyPhotosQuery = new GetMyPhotosQuery(user.getId(), PER_PAGE, currentCount / PER_PAGE + 1);
                view.inject(getMyPhotosQuery);
                return getMyPhotosQuery;
            }
        };
    }
}