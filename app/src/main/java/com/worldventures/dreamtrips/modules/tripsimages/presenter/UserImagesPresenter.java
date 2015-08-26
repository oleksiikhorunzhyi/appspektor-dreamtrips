package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetUserPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;

import java.util.ArrayList;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public class UserImagesPresenter extends TripImagesListPresenter {

    public UserImagesPresenter() {
        super(Type.MEMBER_IMAGES);
    }

    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        return new TripImagesRoboSpiceController() {
            @Override
            public SpiceRequest<ArrayList<IFullScreenObject>> getReloadRequest() {
                return new GetUserPhotosQuery(PER_PAGE, 1);
            }

            @Override
            public SpiceRequest<ArrayList<IFullScreenObject>> getNextPageRequest(int currentCount) {
                return new GetUserPhotosQuery(PER_PAGE, currentCount / PER_PAGE + 1);
            }
        };
    }

}
