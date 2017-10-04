package com.worldventures.dreamtrips.social.ui.reptools.service.analytics;

import com.worldventures.core.model.ShareType;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.AttributeMap;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.dreamtrips.social.util.AnalyticsUtils;

import java.util.HashMap;
import java.util.Map;

@AnalyticsEvent(action = "rep_tools:success_story", trackers = AdobeTracker.TRACKER_KEY)
public class ShareSuccessStoryAction extends BaseAnalyticsAction {

   @Attribute("share") final String share = "1";

   @AttributeMap Map<String, String> attributes = new HashMap<>();

   public ShareSuccessStoryAction(@ShareType String shareType, String storyId) {
      attributes.put(AnalyticsUtils.resolveSharingType(shareType), storyId);
   }
}
