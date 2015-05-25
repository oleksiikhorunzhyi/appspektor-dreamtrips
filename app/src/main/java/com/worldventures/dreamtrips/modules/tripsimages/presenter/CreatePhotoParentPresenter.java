package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.net.Uri;
import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.CreatePhotoFragment;

public class CreatePhotoParentPresenter extends Presenter<Presenter.View> {

    private Uri imageUri;
    private String type;

    public void setImageUri(Uri imageUri, String type) {
        this.imageUri = imageUri;
        this.type = type;
    }

    public void onCreate() {
        Bundle b = new Bundle();
        b.putParcelable(CreatePhotoFragment.BUNDLE_IMAGE_URI, imageUri);
        b.putString(CreatePhotoFragment.BUNDLE_TYPE, type);
        fragmentCompass.add(Route.CREATE_PHOTO, b);
    }
}
