package com.worldventures.dreamtrips.social.ui.reptools.service.analytics

import com.worldventures.core.model.ShareType
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AttributeMap
import com.worldventures.core.service.analytics.BaseAnalyticsAction
import com.worldventures.dreamtrips.social.util.AnalyticsUtils
import com.worldventures.janet.analytics.AnalyticsEvent

import java.util.HashMap

@AnalyticsEvent(action = "rep_tools:success_story", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class ShareSuccessStoryAction(@ShareType shareType: String, storyId: String) : BaseAnalyticsAction() {

   @field:Attribute("share") internal val share = "1"

   @field:AttributeMap internal var attributes: MutableMap<String, String> = HashMap()

   init {
      attributes.put(AnalyticsUtils.resolveSharingType(shareType), storyId)
   }
}
