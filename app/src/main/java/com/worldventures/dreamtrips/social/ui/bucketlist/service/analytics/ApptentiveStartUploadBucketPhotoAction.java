package com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.ApptentiveTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(category = "bl_photo_upload_start", trackers = ApptentiveTracker.TRACKER_KEY)
public class ApptentiveStartUploadBucketPhotoAction extends BaseAnalyticsAction {
}
