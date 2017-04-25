package com.worldventures.dreamtrips.modules.common.delegate;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;
import com.worldventures.dreamtrips.modules.player.service.PodcastDownloadedAnalyticsAction;

import io.techery.janet.Command;

public class GlobalAnalyticEventHandler {

   private AnalyticsInteractor analyticsInteractor;

   private CachedEntityInteractor cachedEntityInteractor;

   public GlobalAnalyticEventHandler(AnalyticsInteractor analyticsInteractor, CachedEntityInteractor cachedEntityInteractor) {
      this.analyticsInteractor = analyticsInteractor;
      this.cachedEntityInteractor = cachedEntityInteractor;

      listenDownloadProgress();
   }

   private void listenDownloadProgress() {
      cachedEntityInteractor.getDownloadCachedModelPipe()
            .observeSuccess()
            .map(Command::getResult)
            .subscribe(cachedEntity -> {
               if (cachedEntity.getEntityClass() == Podcast.class) {
                  BaseAnalyticsAction action = new PodcastDownloadedAnalyticsAction(cachedEntity.getName());
                  analyticsInteractor.analyticsActionPipe().send(action);
               }
            });
   }

}
