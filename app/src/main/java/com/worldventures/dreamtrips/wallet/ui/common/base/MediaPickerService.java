package com.worldventures.dreamtrips.wallet.ui.common.base;

import rx.Observable;

public interface MediaPickerService {
    String SERVICE_NAME = "MediaPickerService";

    Observable<String> pickPhoto();

    Observable<String> pickPhotoAndCrop();

    void hidePicker();
}
