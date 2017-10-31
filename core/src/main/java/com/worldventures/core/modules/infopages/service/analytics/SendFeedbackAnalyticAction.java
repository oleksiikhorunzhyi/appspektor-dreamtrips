package com.worldventures.core.modules.infopages.service.analytics;

import android.support.annotation.Nullable;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.janet.analytics.AnalyticsEvent;

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
