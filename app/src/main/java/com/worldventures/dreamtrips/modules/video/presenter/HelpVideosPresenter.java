package com.worldventures.dreamtrips.modules.video.presenter;

import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.analytics.ViewVideosTabAnalyticAction;
import com.worldventures.dreamtrips.modules.reptools.presenter.TrainingVideosPresenter;
import com.worldventures.dreamtrips.modules.video.service.command.GetMemberVideosCommand;

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
      } else analyticSent = false;
   }

   @Override
   protected boolean isNeedToSendAnalytics() {
      return false;
   }

   @Override
   public void sendAnalytic(String action, String name) {
      // Add analytics when click to video
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
