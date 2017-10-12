package com.worldventures.dreamtrips.modules.common.delegate;

import com.worldventures.core.service.CachedEntityInteractor;
import com.worldventures.core.service.analytics.AnalyticsInteractor;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.dreamtrips.core.janet.DreamTripsCommandServiceWrapper;
import com.worldventures.dreamtrips.core.janet.api_lib.ErrorAnalyticAction;
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast;
import com.worldventures.dreamtrips.social.ui.podcast_player.service.PodcastDownloadedAnalyticsAction;

import io.techery.janet.Command;

public class GlobalAnalyticEventHandler {

   private AnalyticsInteractor analyticsInteractor;

   private CachedEntityInteractor cachedEntityInteractor;
   private DreamTripsCommandServiceWrapper commandServiceWrapper;

   public GlobalAnalyticEventHandler(AnalyticsInteractor analyticsInteractor, CachedEntityInteractor cachedEntityInteractor, DreamTripsCommandServiceWrapper commandServiceWrapper) {
      this.analyticsInteractor = analyticsInteractor;
      this.cachedEntityInteractor = cachedEntityInteractor;
      this.commandServiceWrapper = commandServiceWrapper;

      listenDownloadProgress();
      listenHttpFails();
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

   private void listenHttpFails() {
      commandServiceWrapper.setFailListener((noInternet, path, errorMessage) ->
            analyticsInteractor.analyticsActionPipe().send(noInternet
                  ? ErrorAnalyticAction.trackNoInternetConnection()
                  : ErrorAnalyticAction.trackHttpError(errorMessage, path))
      );
   }

}
