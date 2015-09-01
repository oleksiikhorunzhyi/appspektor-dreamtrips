package com.worldventures.dreamtrips.modules.facebook.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;


public class FacebookPickPhotoPresenter extends Presenter<ActivityPresenter.View> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            fragmentCompass.add(Route.PICK_FB_ALBUM);
        }
    }
}
