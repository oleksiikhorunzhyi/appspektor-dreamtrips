package com.worldventures.dreamtrips.social.ui.reptools.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.social.ui.membership.model.MediaHeader;
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.AdobeTrainingVideosViewedAction;
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ReptoolsVideoDownloadedAction;
import com.worldventures.dreamtrips.social.ui.reptools.service.analytics.ReptoolsVideoViewedAction;
import com.worldventures.dreamtrips.social.ui.video.model.Video;
import com.worldventures.dreamtrips.social.ui.video.model.VideoCategory;
import com.worldventures.dreamtrips.social.ui.video.model.VideoLanguage;
import com.worldventures.dreamtrips.social.ui.video.model.VideoLocale;
import com.worldventures.dreamtrips.social.ui.video.presenter.PresentationVideosPresenter;
import com.worldventures.dreamtrips.social.ui.video.service.command.GetMemberVideosCommand;
import com.worldventures.dreamtrips.social.ui.video.service.command.GetVideoLocalesCommand;

import java.util.List;
import java.util.Locale;

import io.techery.janet.helper.ActionStateSubscriber;

public class TrainingVideosPresenter<T extends TrainingVideosPresenter.View> extends PresentationVideosPresenter<T> {

   private VideoLocale videoLocale = null;
   protected VideoLanguage videoLanguage = null;

   @Override
   public void takeView(T view) {
      super.takeView(view);
      memberVideosInteractor.getVideoLocalesPipe()
            .observeWithReplay()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetVideoLocalesCommand>()
                  .onStart(getVideoLocalesCommand -> view.startLoading())
                  .onFail(this::onFail)
                  .onSuccess(getVideoLocalesCommand -> localesLoaded(getVideoLocalesCommand.getResult())));
   }

   @Override
   public void onResume() {
      videoLocale = db.getLastSelectedVideoLocale();
      videoLanguage = db.getLastSelectedVideoLanguage();
      super.onResume();
      sendViewTrainingVideoAnalytic();
   }

   protected void sendViewTrainingVideoAnalytic() {
      analyticsInteractor.analyticsActionPipe().send(new AdobeTrainingVideosViewedAction());
   }

   @Override
   protected boolean isNeedToSendAnalytics() {
      return false;
   }

   @Override
   protected void loadOnStart() {
      memberVideosInteractor.getVideoLocalesPipe().send(new GetVideoLocalesCommand());
   }

   protected void onFail(CommandWithError commandWithError, Throwable e) {
      super.onFail(commandWithError, e);
      memberVideosInteractor.getVideoLocalesPipe().clearReplays();
   }

   protected void localesLoaded(List<VideoLocale> locales) {
      if (videoLocale == null) {
         videoLocale = getCurrentLocale(locales, context.getResources().getConfiguration().locale);
         if (videoLocale == null) videoLocale = getCurrentLocale(locales, Locale.US);
         if (videoLocale != null) videoLanguage = getCurrentLanguage(videoLocale.getLanguages());
      }
      setHeaderLocale();
      view.setLocales(locales, videoLocale);
      loadVideos();
   }

   private VideoLocale getCurrentLocale(List<VideoLocale> locales, Locale locale) {
      return Queryable.from(locales).firstOrDefault(tempLocale -> tempLocale.getCountry()
            .equalsIgnoreCase(locale.getCountry()));
   }

   private VideoLanguage getCurrentLanguage(List<VideoLanguage> videoLanguages) {
      VideoLanguage videoLanguage = Queryable.from(videoLanguages).firstOrDefault(v -> v.getLocaleName()
            .equalsIgnoreCase(getLocalName()));
      return videoLanguage == null ? videoLanguages.get(0) : videoLanguage;
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
   protected void addCategories(List<VideoCategory> videos) {
      super.addCategories(videos);
      setHeaderLocale();
   }

   private void setHeaderLocale() {
      if (currentItems != null && currentItems.size() > 0) {
         MediaHeader firstHeader = (MediaHeader) currentItems.get(0);
         firstHeader.setVideoLocale(videoLocale);
         firstHeader.setVideoLanguage(videoLanguage);
         view.localeLoaded();
      }
   }

   @Override
   protected void addCategoryHeader(String category, List<Video> videos, int index) {
      currentItems.add(new MediaHeader(category, index == 0));
      currentItems.addAll(videos);
   }

   @Override
   protected String obtainVideoLanguage(Video video) {
      String defaultLocalName = LocaleHelper.formatLocale(LocaleHelper.getDefaultLocale());
      return LocaleHelper.obtainLanguageCode(videoLanguage == null? defaultLocalName : videoLanguage.getLocaleName());
   }

   @Override
   protected GetMemberVideosCommand getMemberVideosRequest() {
      return GetMemberVideosCommand.forRepVideos(videoLanguage);
   }

   @Override
   protected void sendVideoDownloadingAnalytics(Video video) {
      analyticsInteractor.analyticsActionPipe().send(new ReptoolsVideoDownloadedAction(video.getVideoName()));
   }

   @Override
   protected void sendVideoStartedPlayingAnalytics(Video video) {
      analyticsInteractor.analyticsActionPipe().send(new ReptoolsVideoViewedAction(video.getVideoName()));
   }

   public interface View extends PresentationVideosPresenter.View {
      void setLocales(List<VideoLocale> locales, VideoLocale defaultValue);

      void showDialog();

      void localeLoaded();
   }
}
