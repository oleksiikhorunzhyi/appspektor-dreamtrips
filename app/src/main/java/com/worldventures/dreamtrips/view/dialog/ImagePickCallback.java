package com.worldventures.dreamtrips.view.dialog;

import com.kbeanie.imagechooser.api.ChosenImage;

public interface ImagePickCallback {
        void onResult(ChosenImage image, String error);
    }