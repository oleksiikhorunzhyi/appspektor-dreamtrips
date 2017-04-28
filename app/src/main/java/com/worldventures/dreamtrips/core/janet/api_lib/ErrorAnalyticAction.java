package com.worldventures.dreamtrips.core.janet.api_lib;

import com.worldventures.dreamtrips.core.utils.tracksystem.ActionPart;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;

@AnalyticsEvent(action = "errors:${viewState}",
                trackers = AdobeTracker.TRACKER_KEY)
public class ErrorAnalyticAction extends BaseAnalyticsAction {

   @ActionPart String viewState;

   @Attribute("dtaerror") String error = "1";
   @Attribute("errorconditionhttp") String httpError;
   @Attribute("errorconditionapp") String inappError;

   private ErrorAnalyticAction(String viewState) {
      this.viewState = viewState;
   }

   public static ErrorAnalyticAction trackHttpError(String errorResone, String endpoint) {
      ErrorAnalyticAction analyticAction = new ErrorAnalyticAction(endpoint);
      analyticAction.httpError = errorResone;
      return analyticAction;
   }

   public static ErrorAnalyticAction trackNoInternetConnection() {
      ErrorAnalyticAction analyticAction = new ErrorAnalyticAction("nointernetconnection");
      analyticAction.inappError = "no internet connection";
      return analyticAction;
   }

}
