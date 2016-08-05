package com.worldventures.dreamtrips.wallet.ui.common.base;

import java.io.File;

import rx.Observable;

public interface MediaPickerService {
    String SERVICE_NAME = "MediaPickerService";

    void pickPhoto();

    void crop(String filePath);

    void hidePicker();

    Observable<String> observePicker();

    Observable<File> observeCropper();
}
