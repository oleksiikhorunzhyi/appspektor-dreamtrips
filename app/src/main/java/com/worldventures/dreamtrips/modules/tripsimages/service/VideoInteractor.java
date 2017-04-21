package com.worldventures.dreamtrips.modules.tripsimages.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.SendAnalyticsIfNeedAction;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class VideoInteractor {

   private final ActionPipe<SendAnalyticsIfNeedAction> sendAnalyticsIfNeedActionPipe;

   public VideoInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      sendAnalyticsIfNeedActionPipe = sessionActionPipeCreator.createPipe(SendAnalyticsIfNeedAction.class, Schedulers.io());
   }

   public ActionPipe<SendAnalyticsIfNeedAction> sendAnalyticsIfNeedActionPipe() {
      return sendAnalyticsIfNeedActionPipe;
   }
}
