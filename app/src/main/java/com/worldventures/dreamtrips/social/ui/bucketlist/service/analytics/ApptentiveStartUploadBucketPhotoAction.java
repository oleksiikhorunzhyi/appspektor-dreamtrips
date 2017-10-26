package com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics;

import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.ApptentiveTracker;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(category = "bl_photo_upload_start", trackers = ApptentiveTracker.TRACKER_KEY)
public class ApptentiveStartUploadBucketPhotoAction extends BaseAnalyticsAction {
}
