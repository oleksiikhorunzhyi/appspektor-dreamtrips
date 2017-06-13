package com.worldventures.dreamtrips.modules.player.service;

import com.worldventures.dreamtrips.core.utils.tracksystem.ActionPart;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

import org.jetbrains.annotations.Nullable;

@AnalyticsEvent(action = "membership:podcasts:podcast ${progressStatus}",
                trackers = AdobeTracker.TRACKER_KEY)
public class PodcastAnalyticsAction extends BaseAnalyticsAction {

   @ActionPart String progressStatus;

   @Attribute("podcaststart") @Nullable String podcastStart;
   @Attribute("podcast25") @Nullable String podcast25;
   @Attribute("podcast50") @Nullable String podcast50;
   @Attribute("podcast75") @Nullable String podcast75;
   @Attribute("podcast100") @Nullable String podcast100;
   @Attribute("podcastsegment") @Nullable String podcastsegment;

   @Attribute("pcname") String podcastName;

   private PodcastAnalyticsAction(String podcastName) {
      this.podcastName = "dta:" + podcastName;
   }

   public static PodcastAnalyticsAction startPodcast(String podcastName) {
      PodcastAnalyticsAction action = new PodcastAnalyticsAction(podcastName);
      action.progressStatus = "start";
      action.podcastStart = "1";

      return action;
   }

   public static PodcastAnalyticsAction progress25(String podcastName) {
      PodcastAnalyticsAction action = new PodcastAnalyticsAction(podcastName);
      action.progressStatus = "25%";
      action.podcast25 = "1";
      action.podcastsegment = "1:M:0-25";

      return action;
   }

   public static PodcastAnalyticsAction progress50(String podcastName) {
      PodcastAnalyticsAction action = new PodcastAnalyticsAction(podcastName);
      action.progressStatus = "50%";
      action.podcast50 = "1";
      action.podcastsegment = "2:M:25-50";

      return action;
   }

   public static PodcastAnalyticsAction progress75(String podcastName) {
      PodcastAnalyticsAction action = new PodcastAnalyticsAction(podcastName);
      action.progressStatus = "75%";
      action.podcast75 = "1";
      action.podcastsegment = "3:M:50-75";

      return action;
   }

   public static PodcastAnalyticsAction progress100(String podcastName) {
      PodcastAnalyticsAction action = new PodcastAnalyticsAction(podcastName);
      action.progressStatus = "100%";
      action.podcast100 = "1";
      action.podcastsegment = "4:M:75-100";

      return action;
   }

}
