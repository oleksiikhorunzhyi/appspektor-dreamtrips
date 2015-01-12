package com.worldventures.dreamtrips.view.dialog;

import android.support.v4.app.FragmentManager;

import com.facebook.Session;
import com.worldventures.dreamtrips.view.activity.Injector;
import com.worldventures.dreamtrips.view.dialog.facebook.fragment.FacebookAlbumFragment;
import com.worldventures.dreamtrips.view.dialog.facebook.fragment.FacebookSplashFragment;

public class PickImageFacebookDialog {


    private Injector injector;
    private FragmentManager fm;
    private ImagePickCallback callback;

    public PickImageFacebookDialog(Injector injector, FragmentManager fm) {
        this.injector = injector;
        this.fm = fm;
    }


    public void show() {
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            FacebookAlbumFragment facebookAlbumFragment = new FacebookAlbumFragment();
            facebookAlbumFragment.show(fm,injector,callback);
        } else {
            new FacebookSplashFragment().show(fm,injector,callback);
        }
    }

    public void setCallback(ImagePickCallback callback) {
        this.callback = callback;
    }

}
