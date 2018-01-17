package com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics

import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.BaseAnalyticsAction

open class TripImageAnalyticsEvent(@field:Attribute("photo_id") internal val photoId: String) : BaseAnalyticsAction()
