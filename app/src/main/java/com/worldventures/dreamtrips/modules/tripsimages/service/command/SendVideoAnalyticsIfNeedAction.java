package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.analytics.WatchVideoAnalyticAction;
import com.worldventures.dreamtrips.modules.reptools.presenter.TrainingVideosPresenter;
import com.worldventures.dreamtrips.modules.video.presenter.HelpVideosPresenter;
import com.worldventures.dreamtrips.modules.video.presenter.PresentationVideosPresenter;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class SendVideoAnalyticsIfNeedAction extends SendProgressAnalyticsIfNeed<WatchVideoAnalyticAction> implements InjectableAction {

   @Inject AnalyticsInteractor analyticsInteractor;

   private final Class launchComponent;
   private final String language;
   private final String videoName;

   public SendVideoAnalyticsIfNeedAction(Class launchComponent, String language, String videoName, int expectedAnalyticStep, long currentVideoProgress, long totalVideoLength) {
      super(expectedAnalyticStep, currentVideoProgress, totalVideoLength);
      this.launchComponent = launchComponent;
      this.language = language;
      this.videoName = videoName;
   }

   @Override
   protected WatchVideoAnalyticAction chooseAnalyticAction(int currentStep, int expectedAnalyticStep) {
      if (expectedAnalyticStep == 0)
         return WatchVideoAnalyticAction.startVideo(language, videoName, chooseAnalyticNamespace());

      if (currentStep < expectedAnalyticStep) return null;

      WatchVideoAnalyticAction action = null;
      switch (currentStep) {
         case 1:
            action = WatchVideoAnalyticAction.progress25(language, videoName, chooseAnalyticNamespace());
            break;
         case 2:
            action = WatchVideoAnalyticAction.progress50(language, videoName, chooseAnalyticNamespace());
            break;
         case 3:
            action = WatchVideoAnalyticAction.progress75(language, videoName, chooseAnalyticNamespace());
            break;
         case 4:
            action = WatchVideoAnalyticAction.progress100(language, videoName, chooseAnalyticNamespace());
      }
      return action;
   }

   private String chooseAnalyticNamespace() {
      if (launchComponent.equals(HelpVideosPresenter.class)) return WatchVideoAnalyticAction.HELP_VIDEO_NAMESPASE;
      if (launchComponent.equals(PresentationVideosPresenter.class))
         return WatchVideoAnalyticAction.MEMBERSHIP_VIDEOS_NAMESPASE;
      if (launchComponent.equals(TrainingVideosPresenter.class))
         return WatchVideoAnalyticAction.REPTOOLS_TRAINING_VIDEOS_NAMESPASE;
      return null;
   }

   @Override
   protected void sendAnalyticAction(WatchVideoAnalyticAction action) {
      analyticsInteractor.analyticsActionPipe().send(action);
   }
}
