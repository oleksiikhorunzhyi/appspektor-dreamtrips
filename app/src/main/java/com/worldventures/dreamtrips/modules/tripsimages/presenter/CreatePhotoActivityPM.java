package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.net.Uri;
import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.CreatePhotoFragment;

public class CreatePhotoActivityPM extends BasePresenter<BasePresenter.View> {

    private Uri imageUri;

    public CreatePhotoActivityPM(View view) {
        super(view);
    }

    public void onCreate() {
        Bundle b = new Bundle();
        b.putParcelable(CreatePhotoFragment.BUNDLE_IMAGE_URI, imageUri);
        fragmentCompass.add(Route.CREATE_PHOTO, b);
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
