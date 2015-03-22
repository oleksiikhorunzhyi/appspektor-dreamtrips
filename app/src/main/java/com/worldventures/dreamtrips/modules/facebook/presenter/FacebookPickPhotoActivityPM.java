package com.worldventures.dreamtrips.modules.facebook.presenter;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;


public class FacebookPickPhotoActivityPM extends Presenter<ActivityPresenter.View> {

    public FacebookPickPhotoActivityPM(View view) {
        super(view);
    }

    public void create() {
        fragmentCompass.add(Route.PICK_FB_ALBUM);
    }

}
