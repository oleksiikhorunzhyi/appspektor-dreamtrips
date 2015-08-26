package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetForeignPhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.FullScreenPhotoWrapperFragment;

import java.util.ArrayList;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public class ForeignImagesPresenter extends TripImagesListPresenter<TripImagesListPresenter.View> {

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
    protected Bundle getFullscreenArgs(int position) {
        Bundle args = super.getFullscreenArgs(position);
        args.putInt(FullScreenPhotoWrapperFragment.EXTRA_FOREIGN_USER_ID, userId);
        return args;
    }
}