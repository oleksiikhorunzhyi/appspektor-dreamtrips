package com.worldventures.dreamtrips.modules.video.presenter;

import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.modules.reptools.presenter.TrainingVideosPresenter;
import com.worldventures.dreamtrips.modules.video.api.MemberVideosRequest;

public class HelpVideosPresenter extends TrainingVideosPresenter {

    // TODO :: change endpoint after middleware implementation
    @Override
    protected MemberVideosRequest getMemberVideosRequest() {
        if (videoLocale != null && videoLanguage != null)
            return new MemberVideosRequest(DreamTripsApi.TYPE_REP, videoLanguage.getLocaleName());
        else
            return new MemberVideosRequest(DreamTripsApi.TYPE_REP);
    }
}
