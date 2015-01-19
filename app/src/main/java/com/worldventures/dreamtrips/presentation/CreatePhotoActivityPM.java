package com.worldventures.dreamtrips.presentation;

import android.net.Uri;
import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.view.fragment.CreatePhotoFragment;

import org.robobinding.annotation.PresentationModel;

@PresentationModel
public class CreatePhotoActivityPM extends BasePresentation<BasePresentation.View> {

    private Uri imageUri;

    public CreatePhotoActivityPM(View view) {
        super(view);
    }

    public void onCreate() {
        Bundle b = new Bundle();
        b.putParcelable(CreatePhotoFragment.BUNDLE_IMAGE_URI,imageUri);
        fragmentCompass.add(State.CREATE_PHOTO,b);
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Uri getImageUri() {
        return imageUri;
    }
}
