package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.analytics.WatchVideoAnalyticAction;
import com.worldventures.dreamtrips.modules.reptools.presenter.TrainingVideosPresenter;
import com.worldventures.dreamtrips.modules.video.presenter.HelpVideosPresenter;
import com.worldventures.dreamtrips.modules.video.presenter.PresentationVideosPresenter;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class SendAnalyticsIfNeedAction extends Command<Integer> implements InjectableAction {

   @Inject AnalyticsInteractor analyticsInteractor;

   private final Class launchComponent;
   private final String language;
   private final String videoName;

   private int expectedAnalyticStep;
   private final long currentVideoProgress;
   private final long totalVideoLength;

   public SendAnalyticsIfNeedAction(Class launchComponent, String language, String videoName, int expectedAnalyticStep, long currentVideoProgress, long totalVideoLength) {
      this.launchComponent = launchComponent;
      this.language = language;
      this.videoName = videoName;
      this.expectedAnalyticStep = expectedAnalyticStep;
      this.currentVideoProgress = currentVideoProgress;
      this.totalVideoLength = totalVideoLength;
   }

   @Override
   protected void run(CommandCallback<Integer> callback) throws Throwable {
      int percent = (int) ((currentVideoProgress * 100) / totalVideoLength);
      int step = percent / 25;
      WatchVideoAnalyticAction action = chooseAnalyticAction(step, expectedAnalyticStep);
      if (action != null) {
         analyticsInteractor.analyticsActionPipe().send(action);
         callback.onSuccess(expectedAnalyticStep + 1);
      } else
         callback.onSuccess(expectedAnalyticStep);
   }

   private WatchVideoAnalyticAction chooseAnalyticAction(int analyticStep, int currentAnalyticStep) {
      if (currentAnalyticStep == 0)
         return WatchVideoAnalyticAction.startVideo(language, videoName, chooseAnalyticNamespace());

      if (analyticStep < currentAnalyticStep) return null;

      WatchVideoAnalyticAction action = null;
      switch (analyticStep) {
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

}
