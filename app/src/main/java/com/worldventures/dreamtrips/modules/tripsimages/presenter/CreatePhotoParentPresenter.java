package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.net.Uri;
import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.CreatePhotoFragment;

public class CreatePhotoParentPresenter extends ActivityPresenter<ActivityPresenter.View> {

    public void onCreate(Bundle savedInstanceState, Uri imageUri, String type) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle b = new Bundle();
            b.putParcelable(CreatePhotoFragment.BUNDLE_IMAGE_URI, imageUri);
            b.putString(CreatePhotoFragment.BUNDLE_TYPE, type);
            fragmentCompass.add(Route.CREATE_PHOTO, b);
        }
    }
}
