package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.net.Uri;
import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.CreatePhotoFragment;

public class CreatePhotoParentPresenter extends Presenter<Presenter.View> {

    private Uri imageUri;

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public void onCreate() {
        Bundle b = new Bundle();
        b.putParcelable(CreatePhotoFragment.BUNDLE_IMAGE_URI, imageUri);
        fragmentCompass.add(Route.CREATE_PHOTO, b);
    }
}
