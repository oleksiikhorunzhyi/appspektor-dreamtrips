package com.worldventures.dreamtrips.modules.facebook.presenter;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.BaseActivityPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;


public class FacebookPickPhotoActivityPM extends BasePresenter<BaseActivityPresenter.View> {

    public FacebookPickPhotoActivityPM(View view) {
        super(view);
    }

    public void create() {
        fragmentCompass.add(Route.PICK_FB_ALBUM);
    }

}
