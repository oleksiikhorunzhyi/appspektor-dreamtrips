package com.worldventures.dreamtrips.social.ui.profile.service.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "friends_activity", trackers = AdobeTracker.TRACKER_KEY)
public class FriendRelationshipAnalyticAction extends BaseAnalyticsAction {

   @Attribute("unfriend") String unfriendAction;
   @Attribute("reject_friend_request") String rejectAction;
   @Attribute("cancel_friend_request") String cancelAction;

   private FriendRelationshipAnalyticAction() {
   }

   public static FriendRelationshipAnalyticAction cancelRequest() {
      FriendRelationshipAnalyticAction action = new FriendRelationshipAnalyticAction();
      action.cancelAction = "1";
      return action;
   }

   public static FriendRelationshipAnalyticAction rejectRequest() {
      FriendRelationshipAnalyticAction action = new FriendRelationshipAnalyticAction();
      action.rejectAction = "1";
      return action;
   }

   public static FriendRelationshipAnalyticAction unfriend() {
      FriendRelationshipAnalyticAction action = new FriendRelationshipAnalyticAction();
      action.unfriendAction = "1";
      return action;
   }

}
