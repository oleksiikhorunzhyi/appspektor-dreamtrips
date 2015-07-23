package com.worldventures.dreamtrips.modules.reptools.presenter;

import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.membership.presenter.PresentationVideosPresenter;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLanguage;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLocale;
import com.worldventures.dreamtrips.modules.video.api.MemberVideosRequest;

import java.util.ArrayList;

import javax.inject.Inject;

public class TrainingVideosPresenter extends PresentationVideosPresenter<TrainingVideosPresenter.View> {

    VideoLocale videoLocale = null;
    VideoLanguage videoLanguage = null;

    @Inject
    SnappyRepository db;

    @Override
    public void onResume() {
        super.onResume();
        videoLocale = db.getLastSelectedVideoLocale();
        videoLanguage = db.getLastSelectedVideoLanguage();
        loadLocales();

        ArrayList<VideoLocale> locales = new ArrayList<>();
        VideoLocale object = new VideoLocale();
        object.setCountry("United States");
        object.setImage("https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcTCYr5J0mPRwQm5CyH58qddZH3JnkS6QUnCXFIRY6etyb43JLs-iM3Ufrai");
        object.setTitle("USA");
        VideoLanguage videoLanguage = new VideoLanguage();
        videoLanguage.setTitle("English");
        videoLanguage.setCode("us");
        videoLanguage.setNativeTitle("English");
        object.setLanguage(new VideoLanguage[]{videoLanguage, videoLanguage, videoLanguage, videoLanguage});
        locales.add(object);
        locales.add(object);
        locales.add(object);
        view.setLocales(locales, videoLocale);
    }

    private void loadLocales() {
        //TODO wait serverside
        //  doRequest(new GetVideoLocales(), locales -> view.setLocales(locales, videoLocale));
    }

    public void onLanguageSelected(VideoLocale videoLocale, VideoLanguage videoLanguage) {
        this.videoLocale = videoLocale;
        this.videoLanguage = videoLanguage;
        db.saveLastSelectedVideoLocale(videoLocale);
        db.saveLastSelectedVideoLanguage(videoLanguage);
        getAdapterController().reload();
    }

    @Override
    protected MemberVideosRequest getMemberVideosRequest() {
        if (videoLocale != null && videoLanguage != null)
            return new MemberVideosRequest(DreamTripsApi.TYPE_REP, videoLocale.getCountry(), videoLanguage.getCode());
        else return new MemberVideosRequest(DreamTripsApi.TYPE_REP);

    }

    public interface View extends PresentationVideosPresenter.View {
        void setLocales(ArrayList<VideoLocale> locales, VideoLocale defaultValue);
    }
}
