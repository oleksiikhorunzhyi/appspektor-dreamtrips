package com.worldventures.dreamtrips.modules.reptools.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.membership.model.VideoHeader;
import com.worldventures.dreamtrips.modules.video.presenter.PresentationVideosPresenter;
import com.worldventures.dreamtrips.modules.reptools.api.GetVideoLocales;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLanguage;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLocale;
import com.worldventures.dreamtrips.modules.video.api.MemberVideosRequest;
import com.worldventures.dreamtrips.modules.video.model.Category;
import com.worldventures.dreamtrips.modules.video.model.Video;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class TrainingVideosPresenter<T extends TrainingVideosPresenter.View> extends PresentationVideosPresenter<T> {

    protected VideoLocale videoLocale = null;
    protected VideoLanguage videoLanguage = null;

    @Inject
    SnappyRepository db;

    @Override
    public void onResume() {
        videoLocale = db.getLastSelectedVideoLocale();
        videoLanguage = db.getLastSelectedVideoLanguage();
        //
        super.onResume();
        trackAnalyticsOnPostResume();
    }

    protected void trackAnalyticsOnPostResume() {
        TrackingHelper.viewRepToolsTrainingVideoScreen();
    }

    @Override
    protected void loadOnStart() {
        loadLocales();
    }

    private void loadLocales() {
        doRequest(new GetVideoLocales(), this::localesLoaded);
    }

    private void localesLoaded(ArrayList<VideoLocale> locales) {
        if (view != null) {
            if (videoLocale == null) {
                videoLocale = getCurrentLocale(locales, context.getResources().getConfiguration().locale);
                if (videoLocale == null) videoLocale = getCurrentLocale(locales, Locale.US);

                if (videoLocale != null)
                    videoLanguage = getCurrentLanguage(videoLocale.getLanguage());
            }
            setHeaderLocale();
            view.setLocales(locales, videoLocale);
        }
        loadVideos();
    }

    private VideoLocale getCurrentLocale(ArrayList<VideoLocale> locales, Locale locale) {
        return Queryable.from(locales).firstOrDefault(tempLocale ->
                tempLocale.getCountry().equalsIgnoreCase(locale.getCountry()));
    }

    private VideoLanguage getCurrentLanguage(VideoLanguage[] videoLanguages) {
        VideoLanguage videoLanguage = Queryable.from(videoLanguages).firstOrDefault(v ->
                v.getLocaleName().equalsIgnoreCase(getLocalName()));
        return videoLanguage == null ? videoLanguages[0] : videoLanguage;
    }

    private String getLocalName() {
        Locale currentLocale = context.getResources().getConfiguration().locale;
        return String.format("%s-%s", currentLocale.getLanguage(), currentLocale.getCountry()).toLowerCase();
    }

    public void onLanguageSelected(VideoLocale videoLocale, VideoLanguage videoLanguage) {
        this.videoLocale = videoLocale;
        this.videoLanguage = videoLanguage;
        db.saveLastSelectedVideoLocale(videoLocale);
        db.saveLastSelectedVideoLanguage(videoLanguage);
        reload();

        setHeaderLocale();
    }

    @Override
    protected void addCategories(List<Category> videos) {
        super.addCategories(videos);
        setHeaderLocale();
    }

    private void setHeaderLocale() {
        if (currentItems != null && currentItems.size() > 0) {
            VideoHeader firstHeader = (VideoHeader) currentItems.get(0);
            firstHeader.setVideoLocale(videoLocale);
            firstHeader.setVideoLanguage(videoLanguage);
            view.localeLoaded();
        }
    }

    @Override
    protected void addCategoryHeader(String category, List<Video> videos, int index) {
        currentItems.add(new VideoHeader(category, index == 0));
        currentItems.addAll(videos);
    }

    @Override
    public void sendAnalytic(String action, String name) {
        TrackingHelper.actionRepToolsTrainingVideo(action, name);
    }

    @Override
    protected MemberVideosRequest getMemberVideosRequest() {
        if (videoLocale != null && videoLanguage != null)
            return new MemberVideosRequest(DreamTripsApi.TYPE_REP, videoLanguage.getLocaleName());
        else
            return new MemberVideosRequest(DreamTripsApi.TYPE_REP);
    }

    public interface View extends PresentationVideosPresenter.View {

        void setLocales(ArrayList<VideoLocale> locales, VideoLocale defaultValue);

        void showDialog();

        void localeLoaded();
    }
}
