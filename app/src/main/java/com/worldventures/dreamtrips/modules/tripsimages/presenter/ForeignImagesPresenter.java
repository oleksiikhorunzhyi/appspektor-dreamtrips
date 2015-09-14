package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.support.annotation.NonNull;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetForeignPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;

import java.util.ArrayList;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public class ForeignImagesPresenter extends TripImagesListPresenter {

    private int userId;

    public ForeignImagesPresenter(int userId) {
        super(Type.FOREIGN_IMAGES);
        this.userId = userId;
    }

    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        return new TripImagesRoboSpiceController() {
            @Override
            public SpiceRequest<ArrayList<IFullScreenObject>> getReloadRequest() {
                GetForeignPhotosQuery getMyPhotosQuery = new GetForeignPhotosQuery(userId, PER_PAGE, 1);
                view.inject(getMyPhotosQuery);
                return getMyPhotosQuery;
            }

            @Override
            public SpiceRequest<ArrayList<IFullScreenObject>> getNextPageRequest(int currentCount) {
                GetForeignPhotosQuery getMyPhotosQuery = new GetForeignPhotosQuery(userId, PER_PAGE, currentCount / PER_PAGE + 1);
                view.inject(getMyPhotosQuery);
                return getMyPhotosQuery;
            }
        };
    }

    @NonNull
    @Override
    protected FullScreenImagesBundle.Builder getFullscreenArgs(int position) {
        return super.getFullscreenArgs(position).foreignUserId(userId);
    }
}
