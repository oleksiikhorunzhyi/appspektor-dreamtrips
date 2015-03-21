package com.worldventures.dreamtrips.modules.facebook.presenter;

import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.modules.common.presenter.BaseActivityPresentation;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresentation;


public class FacebookPickPhotoActivityPM extends BasePresentation<BaseActivityPresentation.View> {

    public FacebookPickPhotoActivityPM(View view) {
        super(view);
    }

    public void create() {
        fragmentCompass.add(State.PICK_FB_ALBUM);
    }

}
