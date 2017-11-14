package com.worldventures.dreamtrips.social.ui.feed.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ActiveFeedRouteCommand;

import io.techery.janet.ActionPipe;

public class ActiveFeedRouteInteractor {

   private final ActionPipe<ActiveFeedRouteCommand> activeFeedRouteCommandActionPipe;

   public ActiveFeedRouteInteractor(SessionActionPipeCreator creator) {
      this.activeFeedRouteCommandActionPipe = creator.createPipe(ActiveFeedRouteCommand.class);
   }

   public ActionPipe<ActiveFeedRouteCommand> activeFeedRouteCommandActionPipe() {
      return activeFeedRouteCommandActionPipe;
   }
}
