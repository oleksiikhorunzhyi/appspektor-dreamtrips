package com.messenger.ui.util.avatar;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayoutDelegate;

import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

public class ChangeAvatarDelegateImpl implements ChangeAvatarDelegate {

    private PhotoPickerLayoutDelegate photoPickerLayoutDelegate;

    private PublishSubject<ChosenImage> imagesStream = PublishSubject.create();

    public ChangeAvatarDelegateImpl(PhotoPickerLayoutDelegate photoPickerLayoutDelegate) {
        this.photoPickerLayoutDelegate = photoPickerLayoutDelegate;
        initPhotoPicker();
    }

    @Override
    public void showAvatarPhotoPicker() {
        if (!photoPickerLayoutDelegate.isPanelVisible()) {
            photoPickerLayoutDelegate.showPicker();
        }
    }

    @Override
    public void hideAvatarPhotoPicker() {
        photoPickerLayoutDelegate.hidePicker();
    }

    @Override
    public Observable<ChosenImage> getAvatarImagesStream() {
        return imagesStream;
    }

    private void initPhotoPicker() {
        photoPickerLayoutDelegate.setOnDoneClickListener((chosenImages, type) -> onImagesPicked(chosenImages));
    }

    private void onImagesPicked(List<ChosenImage> images) {
        photoPickerLayoutDelegate.hidePicker();
        Queryable.from(images).filter((element, index) -> element != null).forEachR(imagesStream::onNext);
    }
}
