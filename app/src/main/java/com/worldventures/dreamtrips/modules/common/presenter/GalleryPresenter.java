package com.worldventures.dreamtrips.modules.common.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.dtl.location.PermissionView;
import com.worldventures.dreamtrips.modules.feed.api.PhotoGalleryRequest;
import com.worldventures.dreamtrips.modules.feed.event.AttachPhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.OpenFacebookEvent;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.util.ArrayList;

import javax.inject.Inject;

public class GalleryPresenter extends BasePickerPresenter<GalleryPresenter.View> {
    public static final int REQUESTER_ID = -10;

    @Inject
    DrawableUtil drawableUtil;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        loadGallery();
    }

    public void openCamera() {
        if (view.isVisibleOnScreen()) {
            eventBus.post(new ImagePickRequestEvent(PickImageDelegate.CAPTURE_PICTURE, REQUESTER_ID));
        }
    }

    public void onEvent(AttachPhotoEvent event) {
        if (view.isVisibleOnScreen() && event.getRequestType() != -1)
            view.checkPermissions();
    }

    public void onEvent(OpenFacebookEvent event) {
        if (view.isVisibleOnScreen()) {
            view.openFacebookAlbums();
            //
            resetPickedItems();
            view.updatePickedItemsCount(0);
        }
    }

    private void loadGallery() {
        if (photos != null && photos.size() > 0) {
            view.addItems(photos);
            return;
        }

        PhotoGalleryRequest request = new PhotoGalleryRequest(context);
        doRequest(request, photos -> {
            this.photos = new ArrayList<>(photos);
            view.addItems(this.photos);
        });
    }

    private void resetPickedItems() {
        Queryable.from(photos).filter(photo -> photo.isChecked()).forEachR(photo -> photo.setChecked(false));
    }

    public interface View extends BasePickerPresenter.View {

        void openFacebookAlbums();

        void checkPermissions();
    }
}
