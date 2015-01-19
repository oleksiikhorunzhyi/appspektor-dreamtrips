package com.worldventures.dreamtrips.presentation;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.view.dialog.facebook.fragment.FacebookPhotoFragment;

import org.robobinding.annotation.PresentationModel;

@PresentationModel
public class FacebookAlbumFragmentPM extends BasePresentation<BasePresentation.View> {
    public FacebookAlbumFragmentPM(View view) {
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
