package com.worldventures.dreamtrips.modules.infopages.service.analytics;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.utils.tracksystem.ActionPart;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "${nameSpace}:Video ${progressStatus}",
                trackers = AdobeTracker.TRACKER_KEY)
public class WatchVideoAnalyticAction extends BaseAnalyticsAction {

   public static final String REPTOOLS_TRAINING_VIDEOS_NAMESPASE = "rep_tools:training_videos";
   public static final String MEMBERSHIP_VIDEOS_NAMESPASE = "membership:videos";
   public static final String HELP_VIDEO_NAMESPASE = "Help:Videos";

   @Attribute("language") final String language;
   @Attribute("video_id") final String videoId;
   @Attribute("videostart") @Nullable String videoStart;
   @Attribute("video25") @Nullable String video25;
   @Attribute("video50") @Nullable String video50;
   @Attribute("video75") @Nullable String video75;
   @Attribute("video100") @Nullable String video100;
   @Attribute("videosegment") @Nullable String videoSegment;

   @ActionPart String nameSpace;
   @ActionPart String progressStatus;

   private WatchVideoAnalyticAction(String language, String videoName, String nameSpace, String progressStatus) {
      this.language = language;
      this.videoId = "dta:" + videoName;
      this.nameSpace = nameSpace;
      this.progressStatus = progressStatus;
   }

   public static WatchVideoAnalyticAction startVideo(String language, String videoName, String nameSpace) {
      WatchVideoAnalyticAction action = new WatchVideoAnalyticAction(language, videoName, nameSpace, "Start");
      action.videoStart = "1";
      return action;
   }

   public static WatchVideoAnalyticAction progress25(String language, String videoName, String nameSpace) {
      WatchVideoAnalyticAction action = new WatchVideoAnalyticAction(language, videoName, nameSpace, "25%");
      action.video25 = "1";
      action.videoSegment = "1:M:0-25";
      return action;
   }

   public static WatchVideoAnalyticAction progress50(String language, String videoName, String nameSpace) {
      WatchVideoAnalyticAction action = new WatchVideoAnalyticAction(language, videoName, nameSpace, "50%");
      action.video50 = "1";
      action.videoSegment = "2:M:25-50";
      return action;
   }

   public static WatchVideoAnalyticAction progress75(String language, String videoName, String nameSpace) {
      WatchVideoAnalyticAction action = new WatchVideoAnalyticAction(language, videoName, nameSpace, "75%");
      action.video75 = "1";
      action.videoSegment = "3:M:50-75";
      return action;
   }

   public static WatchVideoAnalyticAction progress100(String language, String videoName, String nameSpace) {
      WatchVideoAnalyticAction action = new WatchVideoAnalyticAction(language, videoName, nameSpace, "Complete");
      action.video100 = "1";
      action.videoSegment = "4:M:75-100";
      return action;
   }

}
