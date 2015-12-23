package com.worldventures.dreamtrips.modules.common.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.feed.api.PhotoGalleryRequest;
import com.worldventures.dreamtrips.modules.feed.event.OpenFacebookEvent;

import java.util.ArrayList;

public class GalleryPresenter extends BasePickerPresenter<GalleryPresenter.View> {

    @Override
    public void takeView(View view) {
        super.takeView(view);
        loadGallery();
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
        if (photos != null) {
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
    }
}