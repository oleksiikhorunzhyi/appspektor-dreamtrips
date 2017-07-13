package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.feed.service.command.ActiveFeedRouteCommand;

import io.techery.janet.ActionPipe;

public class ActiveFeedRouteInteractor {

   private ActionPipe<ActiveFeedRouteCommand> activeFeedRouteCommandActionPipe;

   public ActiveFeedRouteInteractor(SessionActionPipeCreator creator) {
      this.activeFeedRouteCommandActionPipe = creator.createPipe(ActiveFeedRouteCommand.class);
   }

   public ActionPipe<ActiveFeedRouteCommand> activeFeedRouteCommandActionPipe() {
      return activeFeedRouteCommandActionPipe;
   }
}
