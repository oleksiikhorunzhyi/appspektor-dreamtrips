package com.worldventures.dreamtrips.modules.reptools.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.AttributeMap;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.modules.common.model.ShareType;

import java.util.HashMap;
import java.util.Map;

@AnalyticsEvent(action = "rep_tools:success_story", trackers = AdobeTracker.TRACKER_KEY)
public class ShareSuccessStoryAction extends BaseAnalyticsAction {

   @Attribute("share")
   final String share = "1";

   @AttributeMap Map<String, String> attributes = new HashMap<>();

   public ShareSuccessStoryAction(@ShareType String shareType, String storyId) {
      attributes.put(AnalyticsUtils.resolveSharingType(shareType), storyId);
   }
}
