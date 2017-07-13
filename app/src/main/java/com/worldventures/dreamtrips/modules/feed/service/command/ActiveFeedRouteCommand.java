package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.wallet.service.command.CachedValueCommand;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ActiveFeedRouteCommand extends CachedValueCommand<Route> {

   public static ActiveFeedRouteCommand update(Route route) {
      return new ActiveFeedRouteCommand(route);
   }

   public static ActiveFeedRouteCommand fetch() {
      return new ActiveFeedRouteCommand();
   }

   protected ActiveFeedRouteCommand() {
      super(cachedRoute -> cachedRoute);
   }

   protected ActiveFeedRouteCommand(Route route) {
      super(cachedRoute -> route);
   }
}
