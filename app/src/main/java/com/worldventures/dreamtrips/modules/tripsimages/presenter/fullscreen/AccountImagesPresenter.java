package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetUserPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;

public class AccountImagesPresenter extends MembersImagesPresenter {

    public AccountImagesPresenter(TripImagesType type, int userId) {
        super(type, userId);
    }

    @Override
    public SpiceRequest<ArrayList<IFullScreenObject>> getReloadRequest() {
        return new GetUserPhotosQuery(userId, PER_PAGE, 1);
    }

    @Override
    public SpiceRequest<ArrayList<IFullScreenObject>> getNextPageRequest(int currentCount) {
        return new GetUserPhotosQuery(userId, PER_PAGE, currentCount / PER_PAGE + 1);
    }

    @Override
    public int getMediaRequestId() {
        return AccountImagesPresenter.class.getSimpleName().hashCode();
    }
}
