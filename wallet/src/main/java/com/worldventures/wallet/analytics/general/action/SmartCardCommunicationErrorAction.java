package com.worldventures.wallet.analytics.general.action;

import com.worldventures.core.service.analytics.ActionPart;
import com.worldventures.core.service.analytics.AdobeTracker;
import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.janet.analytics.AnalyticsEvent;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;

import java.util.Locale;

@AnalyticsEvent(action = "${navigationState}:error", trackers = AdobeTracker.TRACKER_KEY)
public class SmartCardCommunicationErrorAction extends WalletAnalyticsAction {

   @ActionPart String navigationState;

   @Attribute("dtaerror") String error = "1";
   @Attribute("errorcode") String errorCode;

   public SmartCardCommunicationErrorAction(String navigationState, String requestName) {
      this.navigationState = navigationState;
      this.errorCode = String.format(Locale.US, "blecomm-%s", requestName);
   }

   public SmartCardCommunicationErrorAction(String navigationState, String requestName, int errorCode) {
      this.navigationState = navigationState;
      this.errorCode = String.format(Locale.US, "blecomm-%s-%d", requestName, errorCode);
   }

}
