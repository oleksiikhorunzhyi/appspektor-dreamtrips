package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.events.PhotoDeletedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemUpdatedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;

import javax.inject.Inject;

public class BucketFullscreenPresenter extends FullScreenPresenter<BucketPhoto> {


    public BucketFullscreenPresenter() {
    }

    @Inject
    BucketItemManager bucketItemManager;

    BucketItem bucketItem;

    @Override
    public void onInjected() {
        super.onInjected();
        bucketItemManager.setDreamSpiceManager(dreamSpiceManager);
        bucketItem = bucketItemManager.getBucketItemByPhoto(photo);
        if (bucketItem != null && !bucketItem.getUser().equals(getAccount())) bucketItem = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bucketItem != null) view.showCheckbox(bucketItem.getCoverPhoto().equals(photo));
    }

    @Override
    protected boolean isDeleteVisible() {
        return bucketItem != null;
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
        if (bucketItem != null) view.showCheckbox(bucketItem.getCoverPhoto().equals(photo));
    }

    @Override
    public void onDeleteAction() {
        if (bucketItem != null) {
            bucketItemManager.deleteBucketItemPhoto(photo, bucketItem, jsonObject -> {
                view.informUser(context.getString(R.string.photo_deleted));
                eventBus.postSticky(new PhotoDeletedEvent(photo.getFsId()));
            }, this);
        }
    }

    @Override
    public void onCheckboxPressed(boolean status) {
        if (bucketItem != null) {
            if (status && !bucketItem.getCoverPhoto().equals(photo)) {
                view.showCoverProgress();
                bucketItemManager.updateBucketItemCoverId(bucketItem, photo.getFsId(),
                        item ->
                                view.hideCoverProgress(),
                        spiceException -> {
                            this.handleError(spiceException);
                            view.hideCoverProgress();
                        });
            }
        }
    }
}
