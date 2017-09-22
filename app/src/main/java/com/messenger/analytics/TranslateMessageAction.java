package com.messenger.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

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
