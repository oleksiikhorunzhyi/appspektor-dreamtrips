package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.events.PhotoDeletedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemUpdatedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import javax.inject.Inject;

public class BucketFullscreenPresenter extends FullScreenPresenter<BucketPhoto, BucketFullscreenPresenter.View> {

    boolean foreign;

    public BucketFullscreenPresenter(BucketPhoto photo, TripImagesType type, boolean foreign) {
        super(photo, type);
        this.foreign = foreign;
    }

    @Inject
    BucketItemManager bucketItemManager;

    BucketItem bucketItem;

    @Override
    public void onInjected() {
        super.onInjected();
        if (!foreign) {
            bucketItemManager.setDreamSpiceManager(dreamSpiceManager);
            bucketItem = bucketItemManager.getBucketItemByPhoto(photo);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bucketItem != null && bucketItem.getCoverPhoto() != null && !foreign) {
            view.showCheckbox(bucketItem.getCoverPhoto().equals(photo));
        } else {
            view.hideCheckBox();
        }
        if (bucketItem != null) view.showDeleteBtn();
        else view.hideDeleteBtn();
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
                eventBus.postSticky(new PhotoDeletedEvent(photo.getFSId()));
            }, this);
        }
    }


    public void onCheckboxPressed(boolean status) {
        if (bucketItem != null) {
            if (status && !bucketItem.getCoverPhoto().equals(photo)) {
                view.showCoverProgress();
                bucketItemManager.updateBucketItemCoverId(bucketItem, photo.getFSId(),
                        item ->
                                view.hideCoverProgress(),
                        spiceException -> {
                            this.handleError(spiceException);
                            view.hideCoverProgress();
                        });
            }
        }
    }

    public interface View extends FullScreenPresenter.View {
        void showCheckbox(boolean show);

        void showCoverProgress();

        void hideCoverProgress();

        void hideDeleteBtn();

        void showDeleteBtn();

        void hideCheckBox();
    }
}
