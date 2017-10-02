package com.worldventures.dreamtrips.wallet.analytics.oncard.action;

import com.worldventures.core.service.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.AdobeTracker;

import java.util.Locale;

import io.techery.janet.smartcard.model.analytics.AnalyticsLog;
import io.techery.janet.smartcard.model.analytics.AnalyticsLogEntryException;

@AnalyticsEvent(action = "wallet:oncard:exception", trackers = AdobeTracker.TRACKER_KEY)
class SmartCardExceptionAction extends SmartCardAnalyticsAction {

   SmartCardExceptionAction(AnalyticsLog logEntry) {
      super(logEntry);
   }

   @Override
   protected void processLog(int type, AnalyticsLog logEntry) {
      super.processLog(type, logEntry);
      switch (type) {
         case AnalyticsLog.EXCEPTION:
            AnalyticsLogEntryException exception = (AnalyticsLogEntryException) logEntry;

            attributeMap.put("ocerrcat", String.valueOf(exception.exceptionCategory()));
            attributeMap.put("ocerrsubcat", String.valueOf(exception.exceptionType()));
            attributeMap.put("ocerrexcepdetail", String.valueOf(exception.errorCode()));
            attributeMap.put("ocexception", String.format(Locale.US, "%d-%d-%d",
                  exception.exceptionCategory(),
                  exception.exceptionType(),
                  exception.errorCode()));
            break;
      }
   }

}