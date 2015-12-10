package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.util.Log;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetInspireMePhotosQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.util.ArrayList;

public class InspireMePresenter extends TripImagesListPresenter<TripImagesListPresenter.View> {
    protected double randomSeed;

    public InspireMePresenter(int userId) {
        super(TripImagesListFragment.Type.INSPIRE_ME, userId);
    }

    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        return new TripImagesRoboSpiceController() {

            @Override
            public SpiceRequest<ArrayList<IFullScreenObject>> getReloadRequest() {
                randomSeed = Math.random();
                return new GetInspireMePhotosQuery(PER_PAGE, 1, randomSeed);
            }

            @Override
            public SpiceRequest<ArrayList<IFullScreenObject>> getNextPageRequest(int currentCount) {
                Log.d("LoadNext", "count:" + currentCount + "; page: " + ((currentCount / PER_PAGE) + 1));
                return new GetInspireMePhotosQuery(PER_PAGE, currentCount / PER_PAGE + 1, randomSeed);
            }
        };
    }
}