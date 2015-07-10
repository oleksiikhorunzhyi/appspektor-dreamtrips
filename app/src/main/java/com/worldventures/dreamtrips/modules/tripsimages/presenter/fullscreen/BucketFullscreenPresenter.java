package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.events.PhotoDeletedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemUpdatedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoAsCoverRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoDeleteRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;

import javax.inject.Inject;

public class BucketFullscreenPresenter extends FullScreenPresenter<BucketPhoto> {

    @Inject
    BucketItemManager bucketItemManager;

    BucketItem bucketItem;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        bucketItemManager.setDreamSpiceManager(dreamSpiceManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        bucketItem = bucketItemManager.getBucketItemByPhoto(photo);
        view.showCheckbox(bucketItem.getCoverPhoto().equals(photo));
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

    public void onEvent(BucketItemUpdatedEvent event) {
        bucketItem = bucketItemManager.getBucketItemByPhoto(photo);
        view.showCheckbox(bucketItem.getCoverPhoto().equals(photo));
    }

    @Override
    public void onDeleteAction() {
        bucketItemManager.deleteBucketItemPhoto(photo, bucketItem, jsonObject -> {
            view.informUser(context.getString(R.string.photo_deleted));
            eventBus.postSticky(new PhotoDeletedEvent(photo.getFsId()));
        }, this);
    }

    @Override
    public void onCheckboxPressed(boolean status) {
        if (status && !bucketItem.getCoverPhoto().equals(photo)) {
            view.showCoverProgress();
            bucketItemManager.updateBucketItemCoverId(bucketItem, photo.getId(), item ->
                            view.hideCoverProgress(),
                    this);
        }
    }
}
