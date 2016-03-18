package com.messenger.ui.util.avatar;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayoutDelegate;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;

public class ChangeAvatarDelegate implements AvatarImagesProvider {

    @Inject
    PhotoPickerLayoutDelegate photoPickerLayoutDelegate;

    private PublishSubject<ChosenImage> imagesStream = PublishSubject.create();

    public ChangeAvatarDelegate(Injector injector) {
        injector.inject(this);
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
        Queryable.from(images).forEachR(image -> imagesStream.onNext(image));
    }
}
