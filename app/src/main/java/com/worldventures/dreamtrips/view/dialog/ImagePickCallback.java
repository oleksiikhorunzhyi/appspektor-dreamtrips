package com.worldventures.dreamtrips.view.dialog;

import android.support.v4.app.Fragment;

import com.kbeanie.imagechooser.api.ChosenImage;

public interface ImagePickCallback {
    void onResult(Fragment fm,ChosenImage image, String error);
}