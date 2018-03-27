package com.worldventures.dreamtrips.social.ui.tripsimages.service

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics.SendProgressAnalyticsIfNeed

import rx.schedulers.Schedulers

class ProgressAnalyticInteractor(sessionActionPipeCreator: SessionActionPipeCreator) {

   val sendAnalyticsPipe = sessionActionPipeCreator.createPipe(SendProgressAnalyticsIfNeed::class.java, Schedulers.io())
}
