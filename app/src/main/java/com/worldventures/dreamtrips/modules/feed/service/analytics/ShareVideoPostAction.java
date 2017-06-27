package com.worldventures.dreamtrips.modules.feed.service.analytics;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.core.utils.FileUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;

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
