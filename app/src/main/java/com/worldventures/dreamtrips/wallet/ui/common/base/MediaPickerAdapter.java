package com.worldventures.dreamtrips.wallet.ui.common.base;

import com.messenger.delegate.CropImageDelegate;
import com.messenger.ui.util.avatar.MessengerMediaPickerDelegate;

import rx.Observable;

public class MediaPickerAdapter implements MediaPickerService {

    private final MessengerMediaPickerDelegate messengerMediaPickerDelegate;
    private final CropImageDelegate cropImageDelegate;

    public MediaPickerAdapter(MessengerMediaPickerDelegate messengerMediaPickerDelegate, CropImageDelegate cropImageDelegate) {
        this.messengerMediaPickerDelegate = messengerMediaPickerDelegate;
        this.cropImageDelegate = cropImageDelegate;
    }

    @Override
    public Observable<String> pickPhoto() {
        messengerMediaPickerDelegate.showPhotoPicker();
        return messengerMediaPickerDelegate.getImagePathsStream().take(1);
    }

    @Override
    public Observable<String> pickPhotoAndCrop() {
        messengerMediaPickerDelegate.showPhotoPicker();
        return messengerMediaPickerDelegate.getImagePathsStream().take(1)
                .flatMap(imagePath -> {
                    cropImageDelegate.cropImage(imagePath);
                    return cropImageDelegate.getCroppedImagesStream().take(1)
                            .flatMap(cropNotification -> {
                                if (cropNotification.getValue() == null) return Observable.empty();
                                return Observable.just(cropNotification.getValue().getPath());
                            });
                });
    }

    @Override
    public void hidePicker() {
        messengerMediaPickerDelegate.hidePhotoPicker();
    }

}
