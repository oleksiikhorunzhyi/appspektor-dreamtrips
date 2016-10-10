package com.worldventures.dreamtrips.modules.feed.service.analytics;


import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.Hashtag;

@AnalyticsEvent(action = "activity_feed:Post Added",
                trackers = AdobeTracker.TRACKER_KEY)
public class SharePostAction extends BaseAnalyticsAction {
   @Attribute("hashtagging") String hashTags;
   @Attribute("addlocation") String addLocation;

   private SharePostAction() {
   }

   public static SharePostAction createPostAction(TextualPost textualPost) {
      SharePostAction sharePostAction = new SharePostAction();
      if (textualPost.getHashtags() != null && !textualPost.getHashtags().isEmpty()) {
         sharePostAction.hashTags = TextUtils.join(",", Queryable.from(textualPost.getHashtags())
               .map(Hashtag::getName)
               .toArray());
      }
      if (textualPost.getLocation() != null && textualPost.getLocation().getName() != null) {
         sharePostAction.addLocation = "1";
      }
      return sharePostAction;
   }
}
