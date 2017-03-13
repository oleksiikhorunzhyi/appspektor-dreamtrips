package com.worldventures.dreamtrips.modules.infopages.service.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.ActionPart;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "Help:Documents:${documentTitle}",
                trackers = AdobeTracker.TRACKER_KEY)
public class ViewDocumentAnalyticAction extends BaseAnalyticsAction {

   @ActionPart String documentTitle;

   public ViewDocumentAnalyticAction(String documentTitle) {
      this.documentTitle = documentTitle;
   }

}
