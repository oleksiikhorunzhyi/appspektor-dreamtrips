package com.worldventures.dreamtrips.modules.facebook.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookPhotoFragment;

public class FacebookAlbumPresenter extends Presenter<Presenter.View> {

    public void backAction() {
        fragmentCompass.pop();
    }

    public void onItemClick(String fbAlbumId) {
        Bundle b = new Bundle();
        b.putString(FacebookPhotoFragment.BUNDLE_ALBUM_ID, fbAlbumId);
        fragmentCompass.add(Route.PICK_FB_PHOTO, b);
    }
}
