package com.worldventures.dreamtrips.social.ui.reptools.service.analytics

import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.BaseAnalyticsAction
import com.worldventures.janet.analytics.AnalyticsEvent

@AnalyticsEvent(action = "rep_tools:success_story", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class FilterSuccessStoriesShowFavoriteAction : BaseAnalyticsAction() {

   @field:Attribute("favorite") internal val attribute = "1"
}
