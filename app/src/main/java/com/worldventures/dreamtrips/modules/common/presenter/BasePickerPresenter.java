package com.worldventures.dreamtrips.modules.common.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.event.PhotoPickedEvent;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;

public abstract class BasePickerPresenter<T extends BasePickerPresenter.View> extends Presenter<T> {

    @State
    protected ArrayList<BasePhotoPickerModel> photos;
    @Inject
    MediaPickerManager mediaPickerManager;
    private int pickLimit;

    public BasePickerPresenter() {
        this.photos = new ArrayList<>();
    }

    public void onEvent(PhotoPickedEvent event) {
        if (!view.isVisibleOnScreen() || !view.isResumed()) return;
        //
        if (!view.isMultiPickEnabled()) {
            BasePhotoPickerModel photoGalleryModel = Queryable.from(photos).filter(element ->
                    element.isChecked() && !element.equals(event.model)).firstOrDefault();
            if (photoGalleryModel != null) {
                photoGalleryModel.setChecked(false);
                view.updateItem(photoGalleryModel);
            }
        } else {
            if (isLimitReached(Queryable.from(photos).count(BasePhotoPickerModel::isChecked))) {
                event.model.setChecked(false);
                view.informUser(String.format(context.getResources()
                        .getString(R.string.photo_limitation_message), pickLimit));
                return;
            }
        }

        view.updatePickedItemsCount(Queryable.from(photos).count(BasePhotoPickerModel::isChecked));
        view.updateItem(event.model);
    }

    public List<BasePhotoPickerModel> getSelectedPhotos() {
        return Queryable.from(photos).filter(BasePhotoPickerModel::isChecked).toList();
    }

    public void setLimit(int pickLimit) {
        this.pickLimit = pickLimit;
    }

    private boolean isLimitReached(int pickedCount) {
        return pickLimit != 0 && pickedCount > pickLimit;
    }

    public interface View extends Presenter.View {

        void updateItem(BasePhotoPickerModel item);

        void addItems(List<BasePhotoPickerModel> items);

        void updatePickedItemsCount(int count);

        boolean isMultiPickEnabled();

        boolean isResumed();
    }
}
