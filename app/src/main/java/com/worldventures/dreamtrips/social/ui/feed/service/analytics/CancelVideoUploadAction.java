package com.worldventures.dreamtrips.social.ui.feed.service.analytics;

import android.support.annotation.NonNull;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.core.utils.FileUtils;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.social.ui.feed.bundle.CreateEntityBundle;

public abstract class CancelVideoUploadAction extends BaseAnalyticsAction {

   @Attribute("videouploadcancelled") String videouploadcancelled = "1";
   @Attribute("videosize") String videoSize;

   public static CancelVideoUploadAction createAction(PostWithVideoAttachmentBody postWithAttachmentBody) {
      final CancelVideoUploadAction sharePostAction = getSharePhotoPostAction(postWithAttachmentBody.origin());
      sharePostAction.videoSize = String.valueOf(FileUtils.bytesToMB(postWithAttachmentBody.size()));
      return sharePostAction;
   }

   @NonNull
   private static CancelVideoUploadAction getSharePhotoPostAction(CreateEntityBundle.Origin origin) {
      CancelVideoUploadAction sharePostAction;
      switch (origin) {
         case FEED:
            sharePostAction = new CancelVideoUploadAction.CancelVideoUploadFeedAction();
            break;
         default:
            sharePostAction = new CancelVideoUploadAction.CancelVideoUploadMyProfileAction();
            break;
      }
      return sharePostAction;
   }

   @AnalyticsEvent(action = "activity_feed:Video Upload Complete",
                   trackers = AdobeTracker.TRACKER_KEY)
   public static final class CancelVideoUploadFeedAction extends CancelVideoUploadAction {
   }

   @AnalyticsEvent(action = "My Profile:Video Upload Complete",
                   trackers = AdobeTracker.TRACKER_KEY)
   public static final class CancelVideoUploadMyProfileAction extends CancelVideoUploadAction {
   }
}
