package com.worldventures.dreamtrips.modules.tripsimages.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.SendProgressAnalyticsIfNeed;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.SendVideoAnalyticsIfNeedAction;

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
