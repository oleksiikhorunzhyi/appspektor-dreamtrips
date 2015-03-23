package com.worldventures.dreamtrips.modules.facebook.presenter;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;


public class FacebookPickPhotoPresenter extends Presenter<ActivityPresenter.View> {

    public FacebookPickPhotoPresenter(View view) {
        super(view);
    }

    public void create() {
        fragmentCompass.add(Route.PICK_FB_ALBUM);
    }

}
