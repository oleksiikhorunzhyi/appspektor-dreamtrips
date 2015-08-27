package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetMyPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;

import java.util.ArrayList;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public class AccountImagesPresenter extends UserImagesPresenter {

    public AccountImagesPresenter() {
        this(Type.MY_IMAGES);
    }

    public AccountImagesPresenter(Type type) {
        super(type);
    }

    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        User user = appSessionHolder.get().get().getUser();

        return new TripImagesRoboSpiceController() {
            @Override
            public SpiceRequest<ArrayList<IFullScreenObject>> getReloadRequest() {
                GetMyPhotosQuery getMyPhotosQuery = new GetMyPhotosQuery(user.getId(), PER_PAGE, 1);
                view.inject(getMyPhotosQuery);
                return getMyPhotosQuery;
            }

            @Override
            public SpiceRequest<ArrayList<IFullScreenObject>> getNextPageRequest(int currentCount) {
                GetMyPhotosQuery getMyPhotosQuery = new GetMyPhotosQuery(user.getId(), PER_PAGE, currentCount / PER_PAGE + 1);
                view.inject(getMyPhotosQuery);
                return getMyPhotosQuery;
            }
        };
    }

}
