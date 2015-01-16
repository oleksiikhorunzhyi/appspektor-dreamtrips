package com.worldventures.dreamtrips.view.dialog;

import android.support.v4.app.FragmentManager;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.view.dialog.facebook.fragment.FacebookAlbumFragment;

public class PickImageFacebookDialog {

    private Injector injector;
    private FragmentManager fm;
    private ImagePickCallback callback;

    public PickImageFacebookDialog(Injector injector, FragmentManager fm) {
        this.injector = injector;
        this.fm = fm;
    }

    public void show() {
        FacebookAlbumFragment facebookAlbumFragment = new FacebookAlbumFragment();
        facebookAlbumFragment.show(fm, injector, callback);
    }

    public void setCallback(ImagePickCallback callback) {
        this.callback = callback;
    }
}
