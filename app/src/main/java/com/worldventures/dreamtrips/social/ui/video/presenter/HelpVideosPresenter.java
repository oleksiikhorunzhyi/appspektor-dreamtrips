package com.worldventures.dreamtrips.social.ui.video.presenter;

import com.worldventures.core.modules.video.model.Video;
import com.worldventures.core.modules.video.service.command.GetMemberVideosCommand;
import com.worldventures.core.service.analytics.AnalyticsInteractor;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.social.ui.infopages.service.analytics.ViewVideosTabAnalyticAction;
import com.worldventures.dreamtrips.social.ui.reptools.presenter.TrainingVideosPresenter;

import java.util.List;

import javax.inject.Inject;

public class HelpVideosPresenter extends TrainingVideosPresenter<HelpVideosPresenter.View> {

   @Inject AnalyticsInteractor analyticsInteractor;

   private boolean analyticSent = true;

   @Override
   protected GetMemberVideosCommand getMemberVideosRequest() {
      return GetMemberVideosCommand.forHelpVideos(videoLanguage);
   }

   @Override
   public void track() {
      if (videoLanguage != null) {
         sendViewAnalytics();
         analyticSent = true;
      } else {
         analyticSent = false;
      }
   }

   @Override
   protected boolean isNeedToSendAnalytics() {
      return false;
   }

   @Override
   protected void sendVideoDownloadingAnalytics(Video video) {
   }

   @Override
   protected void sendVideoStartedPlayingAnalytics(Video video) {
   }

   @Override
   protected void sendViewTrainingVideoAnalytic() {
   }

   @Override
   protected void localesLoaded(List list) {
      super.localesLoaded(list);
      if (!analyticSent) {
         sendViewAnalytics();
         analyticSent = true;
      }
   }

   private void sendViewAnalytics() {
      String language = LocaleHelper.obtainLanguageCode(videoLanguage.getLocaleName());
      analyticsInteractor.analyticsActionPipe().send(new ViewVideosTabAnalyticAction(language));
   }

   public interface View extends TrainingVideosPresenter.View {
   }

}
