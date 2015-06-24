package com.worldventures.dreamtrips.modules.reptools.presenter;

import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.membership.presenter.PresentationVideosPresenter;
import com.worldventures.dreamtrips.modules.reptools.api.GetVideoLocales;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLocale;
import com.worldventures.dreamtrips.modules.video.api.MemberVideosRequest;

import java.util.ArrayList;

import javax.inject.Inject;

public class TrainingVideosPresenter extends PresentationVideosPresenter<TrainingVideosPresenter.View> {

    VideoLocale videoLocale = null;

    @Inject
    SnappyRepository db;

    @Override
    public void onResume() {
        super.onResume();
        videoLocale = db.getLastSelectedVideoLocale();
        loadLocales();
    }

    private void loadLocales() {
        doRequest(new GetVideoLocales(), locales -> view.setLocales(locales, videoLocale));
    }

    public void onLanguageSelected(VideoLocale videoLocale) {
        this.videoLocale = videoLocale;
        db.saveLastSelectedVideoLocale(videoLocale);
        getAdapterController().reload();
    }

    @Override
    protected MemberVideosRequest getMemberVideosRequest() {
        if (videoLocale != null)
            return new MemberVideosRequest(DreamTripsApi.TYPE_REP, videoLocale.getCountry());
        else return new MemberVideosRequest(DreamTripsApi.TYPE_REP);

    }

    public interface View extends PresentationVideosPresenter.View {
        void setLocales(ArrayList<VideoLocale> locales, VideoLocale defaultValue);
    }
}
