package com.worldventures.dreamtrips.presentation;

import android.net.Uri;

import com.worldventures.dreamtrips.core.navigation.State;

import org.robobinding.annotation.PresentationModel;

@PresentationModel
public class CreatePhotoActivityPM extends BasePresentation<BasePresentation.View> {

    private Uri imageUri;

    public CreatePhotoActivityPM(View view) {
        super(view);
    }

    public void onCreate() {
        fragmentCompass.add(State.CREATE_PHOTO);
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Uri getImageUri() {
        return imageUri;
    }
}
