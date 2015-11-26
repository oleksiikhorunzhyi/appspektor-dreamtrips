package com.worldventures.dreamtrips.modules.common.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.worldventures.dreamtrips.modules.common.event.PhotoPickedEvent;
import com.worldventures.dreamtrips.modules.feed.api.PhotoGalleryRequest;
import com.worldventures.dreamtrips.modules.feed.model.PhotoGalleryModel;

import java.util.ArrayList;
import java.util.List;

import icepick.State;

public class PhotoPickerPresenter extends Presenter<PhotoPickerPresenter.View> {

    @State
    ArrayList<PhotoGalleryModel> photos;

    public void onEvent(PhotoPickedEvent event) {
        if (!view.isMultiPickEnabled()) {
            PhotoGalleryModel photoGalleryModel = Queryable.from(photos).filter(element ->
                    element.isChecked() && !element.equals(event.model)).firstOrDefault();
            if (photoGalleryModel != null) {
                photoGalleryModel.setChecked(false);
                view.updateItem(photoGalleryModel);
            }
        }

        view.updatePickedItemsCount(Queryable.from(photos).count(PhotoGalleryModel::isChecked));
        view.updateItem(event.model);
    }

    public void loadGallery() {
        if (photos != null) {
            return;
        }

        PhotoGalleryRequest request = new PhotoGalleryRequest(context);
        doRequest(request, photos -> {
            this.photos = photos;
            view.initPhotos(this.photos);
        });
    }

    public List<ChosenImage> getSelectedPhotos() {
        return Queryable.from(photos).filter(PhotoGalleryModel::isChecked).map(element -> {
            ChosenImage chosenImage = new ChosenImage();
            chosenImage.setFileThumbnail(element.getThumbnailPath());
            chosenImage.setFilePathOriginal(element.getOriginalPath());

            return chosenImage;
        }).toList();
    }

    public void cancelAllSelections() {
        if (photos == null) {
            return;
        }

        Queryable.from(photos).filter(PhotoGalleryModel::isChecked).forEachR(model -> model.setChecked(false));

        view.updatePickedItemsCount(0);
    }

    public interface View extends Presenter.View {

        void updatePickedItemsCount(int pickedCount);

        void updateItem(PhotoGalleryModel item);

        void initPhotos(List<PhotoGalleryModel> photos);

        boolean isMultiPickEnabled();
    }
}
