package com.messenger.ui.util.avatar;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.messenger.ui.helper.LegacyPhotoPickerDelegate;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayoutDelegate;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

public class MessengerMediaPickerDelegateImpl implements MessengerMediaPickerDelegate {

    private LegacyPhotoPickerDelegate legacyPhotoPickerDelegate;
    private PhotoPickerLayoutDelegate photoPickerLayoutDelegate;

    private PublishSubject<String> imagesStream = PublishSubject.create();
    private Subscription cameraImagesStreamSubscription;

    public MessengerMediaPickerDelegateImpl(LegacyPhotoPickerDelegate legacyPhotoPickerDelegate,
                                            PhotoPickerLayoutDelegate photoPickerLayoutDelegate) {
        this.legacyPhotoPickerDelegate = legacyPhotoPickerDelegate;
        this.photoPickerLayoutDelegate = photoPickerLayoutDelegate;
        initPhotoPicker();
    }

    @Override
    public void register() {
        legacyPhotoPickerDelegate.register();
        cameraImagesStreamSubscription = legacyPhotoPickerDelegate
                .watchChosenImages()
                .subscribe(photos -> {
                    onImagesPicked(Queryable.from(photos)
                            .map(ChosenImage::getFilePathOriginal)
                            .toList());
                });
    }

    @Override
    public void unregister() {
        legacyPhotoPickerDelegate.unregister();
        cameraImagesStreamSubscription.unsubscribe();
    }

    @Override
    public void showPhotoPicker() {
        if (!photoPickerLayoutDelegate.isPanelVisible()) {
            photoPickerLayoutDelegate.showPicker();
        }
    }

    @Override
    public void hidePhotoPicker() {
        photoPickerLayoutDelegate.hidePicker();
    }

    @Override
    public Observable<String> getImagePathsStream() {
        return imagesStream;
    }

    private void initPhotoPicker() {
        photoPickerLayoutDelegate.setOnDoneClickListener((chosenImages, type) ->
                onImagesPicked(Queryable.from(chosenImages)
                        .map(BasePhotoPickerModel::getOriginalPath)
                        .toList()));
    }

    private void onImagesPicked(List<String> imagePaths) {
        photoPickerLayoutDelegate.hidePicker();
        Queryable.from(imagePaths)
                .forEachR(path -> imagesStream.onNext(path));
    }
}
