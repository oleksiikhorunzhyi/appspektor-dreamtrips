package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetUserPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;

import java.util.ArrayList;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public class AccountImagesPresenter extends MemberImagesPresenter {

    public AccountImagesPresenter(Type type, int userId) {
        super(type, userId);
    }

    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        return new TripImagesRoboSpiceController() {
            @Override
            public SpiceRequest<ArrayList<IFullScreenObject>> getReloadRequest() {
                GetUserPhotosQuery getMembersPhotosQuery = new GetUserPhotosQuery(userId, PER_PAGE, 1);
                return getMembersPhotosQuery;
            }

            @Override
            public SpiceRequest<ArrayList<IFullScreenObject>> getNextPageRequest(int currentCount) {
                GetUserPhotosQuery getMembersPhotosQuery = new GetUserPhotosQuery(userId, PER_PAGE, currentCount / PER_PAGE + 1);
                return getMembersPhotosQuery;
            }
        };
    }

}
