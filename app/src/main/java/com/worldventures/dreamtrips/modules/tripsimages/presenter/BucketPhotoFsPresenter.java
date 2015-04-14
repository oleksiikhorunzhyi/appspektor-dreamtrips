package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

public class BucketPhotoFsPresenter extends TripImagesListPM<BucketPhoto> {
    public BucketPhotoFsPresenter(View view) {
        super(view, TripImagesListFragment.Type.BUCKET_PHOTOS);
    }

    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        return null;
    }
}
