package com.worldventures.dreamtrips.modules.player.service;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.SendProgressAnalyticsIfNeed;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class SendPodcastAnalyticsIfNeedAction extends SendProgressAnalyticsIfNeed<PodcastAnalyticsAction> implements InjectableAction {

   @Inject AnalyticsInteractor analyticsInteractor;

   private String podcastName;

   public SendPodcastAnalyticsIfNeedAction(String podcastName, int expectedAnalyticStep, long currentProgress, long totalLength) {
      super(expectedAnalyticStep, currentProgress, totalLength);
      this.podcastName = podcastName;
   }

   @Override
   protected PodcastAnalyticsAction chooseAnalyticAction(int currentStep, int expectedAnalyticStep) {
      if (expectedAnalyticStep == 0)
         return PodcastAnalyticsAction.startPodcast(podcastName);

      if (currentStep < expectedAnalyticStep) return null;

      PodcastAnalyticsAction action = null;
      switch (currentStep) {
         case 1:
            action = PodcastAnalyticsAction.progress25(podcastName);
            break;
         case 2:
            action = PodcastAnalyticsAction.progress50(podcastName);
            break;
         case 3:
            action = PodcastAnalyticsAction.progress75(podcastName);
            break;
         case 4:
            action = PodcastAnalyticsAction.progress100(podcastName);
      }
      return action;
   }

   @Override
   protected void sendAnalyticAction(PodcastAnalyticsAction action) {
      analyticsInteractor.analyticsActionPipe().send(action);
   }
}
