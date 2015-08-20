package com.worldventures.dreamtrips.modules.reptools.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.membership.model.VideoHeader;
import com.worldventures.dreamtrips.modules.membership.presenter.PresentationVideosPresenter;
import com.worldventures.dreamtrips.modules.reptools.api.GetVideoLocales;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLanguage;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLocale;
import com.worldventures.dreamtrips.modules.video.api.MemberVideosRequest;
import com.worldventures.dreamtrips.modules.video.event.LanguageClickedEvent;
import com.worldventures.dreamtrips.modules.video.model.Category;
import com.worldventures.dreamtrips.modules.video.model.Video;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class TrainingVideosPresenter extends PresentationVideosPresenter<TrainingVideosPresenter.View> {

    VideoLocale videoLocale = null;
    VideoLanguage videoLanguage = null;

    @Inject
    SnappyRepository db;

    @Override
    public void onResume() {
        videoLocale = db.getLastSelectedVideoLocale();
        videoLanguage = db.getLastSelectedVideoLanguage();
        super.onResume();
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
                    videoLanguage = Queryable.from(videoLocale.getLanguage()).firstOrDefault();

            }

            setHeaderLocale();
            view.setLocales(locales, videoLocale);
        }
    }

    private VideoLocale getCurrentLocale(ArrayList<VideoLocale> locales, Locale locale) {
        return Queryable.from(locales).firstOrDefault(tempLocale ->
                tempLocale.getCountry().equalsIgnoreCase(locale.getCountry()));
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
            view.getAdapter().notifyItemChanged(0);
        }
    }

    @Override
    protected void addCategoryHeader(String category, List<Video> videos, int index) {
        currentItems.add(new VideoHeader(category, index == 0));
        currentItems.addAll(videos);
    }

    public void onEvent(LanguageClickedEvent event) {
        view.showDialog();
    }

    @Override
    protected MemberVideosRequest getMemberVideosRequest() {
        if (videoLocale != null && videoLanguage != null)
            return new MemberVideosRequest(DreamTripsApi.TYPE_REP, videoLanguage.getLocaleName());
        else return new MemberVideosRequest(DreamTripsApi.TYPE_REP);

    }

    public interface View extends PresentationVideosPresenter.View {
        void setLocales(ArrayList<VideoLocale> locales, VideoLocale defaultValue);

        void showDialog();
    }
}
