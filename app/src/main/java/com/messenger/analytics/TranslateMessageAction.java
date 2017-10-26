package com.messenger.analytics;

import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

@AnalyticsEvent(action = "Messenger:View Conversation", trackers = AdobeTracker.TRACKER_KEY)
public class TranslateMessageAction extends BaseAnalyticsAction {

   @Attribute("translation")
   String translation = "1";

   @Attribute("translated")
   String translatedLanguage;

   public TranslateMessageAction(String translatedLanguage) {
      this.translatedLanguage = translatedLanguage;
   }
}
