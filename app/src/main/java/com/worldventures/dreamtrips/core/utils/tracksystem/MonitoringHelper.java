package com.worldventures.dreamtrips.core.utils.tracksystem;

import com.newrelic.agent.android.NewRelic;

public class MonitoringHelper {

   public static void startInteractionName(Object interactor) {
      NewRelic.startInteraction(getInteractionName(interactor));
   }

   public static void setInteractionName(Object interactor) {
      NewRelic.setInteractionName(getInteractionName(interactor));
   }

   private static String getInteractionName(Object interactor) {
      return interactor.getClass().getSimpleName();
   }
}
