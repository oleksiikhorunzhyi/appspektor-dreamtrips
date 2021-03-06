package com.worldventures.dreamtrips.core.utils;


import com.worldventures.core.service.analytics.AnalyticsInteractor;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;

import java.util.ArrayList;
import java.util.List;

/**
 * This class restricts sending an analytic actions with equal params.
 * Before using this proxy correctly you have to override equals() method of analytic actions.
 */
public class AnalyticsInteractorProxy {

   private final AnalyticsInteractor analyticsInteractor;
   private final List<BaseAnalyticsAction> capturedAnalytics;

   public AnalyticsInteractorProxy(AnalyticsInteractor analyticsInteractor) {
      this.analyticsInteractor = analyticsInteractor;
      capturedAnalytics = new ArrayList<>();
   }

   public void sendCommonAnalytic(BaseAnalyticsAction baseAnalyticsAction) {
      if (!capturedAnalytics.contains(baseAnalyticsAction)) {
         analyticsInteractor.analyticsActionPipe().send(baseAnalyticsAction);
         capturedAnalytics.add(baseAnalyticsAction);
      }
   }

}
