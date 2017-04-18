package com.worldventures.dreamtrips.modules.infopages.service.analytics;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "Send Feedback",
                trackers = AdobeTracker.TRACKER_KEY)
public class SendFeedbackAnalyticAction extends BaseAnalyticsAction {

   @Attribute("feedbackreason") final int reasonNumber;
   @Attribute("photoupload") @Nullable Integer existAttachments;
   @Attribute("uploadamt") @Nullable Integer attachmentsAmmount;


   public SendFeedbackAnalyticAction(int reasonNumber, int attachmentsAmmount) {
      this.reasonNumber = reasonNumber;
      if (attachmentsAmmount > 0) {
         this.existAttachments = 1;
         this.attachmentsAmmount = attachmentsAmmount;
      }
   }
}
