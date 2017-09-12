package com.worldventures.dreamtrips.modules.profile.service.analytics;


import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

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
