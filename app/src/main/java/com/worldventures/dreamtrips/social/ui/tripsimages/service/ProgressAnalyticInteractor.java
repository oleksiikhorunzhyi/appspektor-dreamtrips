package com.worldventures.dreamtrips.social.ui.tripsimages.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.SendProgressAnalyticsIfNeed;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class ProgressAnalyticInteractor {

   private final ActionPipe<SendProgressAnalyticsIfNeed> sendAnalyticsIfNeedActionPipe;

   public ProgressAnalyticInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      sendAnalyticsIfNeedActionPipe = sessionActionPipeCreator.createPipe(SendProgressAnalyticsIfNeed.class, Schedulers.io());
   }

   public ActionPipe<SendProgressAnalyticsIfNeed> sendProgressAnalyticsIfNeedActionPipe() {
      return sendAnalyticsIfNeedActionPipe;
   }
}
