package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoAsCoverRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoDeleteRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;

public class BucketFullscreenPresenter extends FullScreenPresenter<BucketPhoto> {

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.showCheckbox(photo.isCover());
    }

    @Override
    protected boolean isDeleteVisible() {
        return true;
    }

    @Override
    protected boolean isFlagVisible() {
        return false;
    }

    @Override
    protected boolean isLikeVisible() {
        return false;
    }

    @Override
    public void onDeleteAction() {
        eventBus.post(new BucketPhotoDeleteRequestEvent(photo));
    }

    @Override
    public void onCheckboxPressed() {
        eventBus.post(new BucketPhotoAsCoverRequestEvent(photo));
    }
}
