package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

public class BucketPhotoFsPresenter extends TripImagesListPresenter<BucketPhoto> {

    public BucketPhotoFsPresenter() {
        super(TripImagesListFragment.Type.BUCKET_PHOTOS);
    }

    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        return null;
    }
}
