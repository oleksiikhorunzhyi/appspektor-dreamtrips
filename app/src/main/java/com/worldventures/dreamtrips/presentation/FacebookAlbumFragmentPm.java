package com.worldventures.dreamtrips.presentation;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.view.dialog.facebook.fragment.FacebookPhotoFragment;

public class FacebookAlbumFragmentPm extends BasePresentation<BasePresentation.View> {
    public FacebookAlbumFragmentPm(View view) {
        super(view);
    }

    public void backAction() {
        fragmentCompass.pop();
    }

    public void onItemClick(String fbAlbumId) {
        Bundle b = new Bundle();
        b.putString(FacebookPhotoFragment.BUNDLE_ALBUM_ID, fbAlbumId);
        fragmentCompass.add(State.PICK_FB_PHOTO, b);
    }
}
