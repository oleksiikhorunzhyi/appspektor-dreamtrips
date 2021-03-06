package com.worldventures.dreamtrips.social.ui.feed.service.analytics;

import android.support.annotation.NonNull;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.core.utils.FileUtils;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.social.ui.feed.bundle.CreateEntityBundle;

public class ShareVideoPostAction extends BaseAnalyticsAction {
   @Attribute("videouploadcomplete") String videoupload = "1";
   @Attribute("videouploadtime") String videoUploadTime;
   @Attribute("videosize") String videoSize;
   @Attribute("videolength") String videoLength;

   private ShareVideoPostAction() {
   }

   public static ShareVideoPostAction createPostAction(PostWithVideoAttachmentBody postWithAttachmentBody) {
      final ShareVideoPostAction sharePostAction = getSharePhotoPostAction(postWithAttachmentBody.origin());
      sharePostAction.videoLength = String.valueOf(postWithAttachmentBody.durationInSeconds());
      sharePostAction.videoUploadTime = String.valueOf(postWithAttachmentBody.uploadTime());
      sharePostAction.videoSize = String.valueOf(FileUtils.bytesToMB(postWithAttachmentBody.size()));
      return sharePostAction;
   }

   @NonNull
   private static ShareVideoPostAction getSharePhotoPostAction(CreateEntityBundle.Origin origin) {
      ShareVideoPostAction sharePostAction;
      switch (origin) {
         case FEED:
            sharePostAction = new ShareVideoPostAction.ShareVideoPostFromFeedAction();
            break;
         default:
            sharePostAction = new ShareVideoPostAction.ShareVideoPostFromProfileAction();
            break;
      }
      return sharePostAction;
   }

   @AnalyticsEvent(action = "activity_feed:Video Upload Complete",
                   trackers = AdobeTracker.TRACKER_KEY)
   public static class ShareVideoPostFromFeedAction extends ShareVideoPostAction {
   }

   @AnalyticsEvent(action = "My Profile:Video Upload Complete",
                   trackers = AdobeTracker.TRACKER_KEY)
   public static class ShareVideoPostFromProfileAction extends ShareVideoPostAction {
   }
}
