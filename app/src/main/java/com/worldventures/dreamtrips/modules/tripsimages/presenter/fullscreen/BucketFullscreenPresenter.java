package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.events.PhotoDeletedEvent;
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
        view.informUser(context.getString(R.string.photo_deleted));
        eventBus.postSticky(new PhotoDeletedEvent(photo.getFsId()));
    }

    @Override
    public void onCheckboxPressed(boolean status) {
        if (status && !photo.isCover())
            eventBus.postSticky(new BucketPhotoAsCoverRequestEvent(photo));
    }
}
