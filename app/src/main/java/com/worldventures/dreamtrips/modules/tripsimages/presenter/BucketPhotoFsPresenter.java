package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoAsCoverRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.CoverSetEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.util.List;

public class BucketPhotoFsPresenter extends TripImagesListPresenter<BucketPhoto> {

    public BucketPhotoFsPresenter() {
        super(TripImagesListFragment.Type.BUCKET_PHOTOS);
    }

    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        return null;
    }

    public void onEvent(CoverSetEvent event) {
        eventBus.cancelEventDelivery(event);
        List<IFullScreenObject> photos = view.getPhotosFromAdapter();
        Queryable.from(photos).forEachR(photo -> {
            if (photo instanceof BucketPhoto) {
                BucketPhoto bucketPhoto = (BucketPhoto) photo;
                bucketPhoto.setIsCover(event.getCoverId() == bucketPhoto.getId());
            }
        });
        view.refresh();
    }
}
