package com.worldventures.dreamtrips.wallet.ui.common.base;

import com.messenger.delegate.CropImageDelegate;
import com.messenger.ui.util.avatar.MessengerMediaPickerDelegate;

import java.io.File;

import rx.Notification;
import rx.Observable;

public class MediaPickerAdapter implements MediaPickerService {

    private final MessengerMediaPickerDelegate messengerMediaPickerDelegate;
    private final CropImageDelegate cropImageDelegate;

    public MediaPickerAdapter(MessengerMediaPickerDelegate messengerMediaPickerDelegate, CropImageDelegate cropImageDelegate) {
        this.messengerMediaPickerDelegate = messengerMediaPickerDelegate;
        this.cropImageDelegate = cropImageDelegate;
    }

    @Override
    public void pickPhoto() {
        messengerMediaPickerDelegate.showPhotoPicker();
    }

    @Override
    public void crop(String filePath) {
        cropImageDelegate.cropImage(filePath);
    }

    @Override
    public void hidePicker() {
        messengerMediaPickerDelegate.hidePhotoPicker();
    }

    @Override
    public Observable<String> observePicker() {
        return messengerMediaPickerDelegate.getImagePathsStream();
    }

    @Override
    public Observable<File> observeCropper() {
        return cropImageDelegate.getCroppedImagesStream()
                .filter(cropNotification -> cropNotification.getKind() == Notification.Kind.OnNext)
                .map(Notification::getValue);
    }

}
