package com.worldventures.dreamtrips.social.ui.feed.service.command;

import com.worldventures.dreamtrips.social.ui.feed.view.cell.util.FeedCellListWidthProvider;
import com.worldventures.wallet.service.command.CachedValueCommand;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ActiveFeedRouteCommand extends CachedValueCommand<FeedCellListWidthProvider.FeedType> {

   public static ActiveFeedRouteCommand update(FeedCellListWidthProvider.FeedType route) {
      return new ActiveFeedRouteCommand(route);
   }

   public static ActiveFeedRouteCommand fetch() {
      return new ActiveFeedRouteCommand();
   }

   private ActiveFeedRouteCommand() {
      super(cachedRoute -> cachedRoute);
   }

   protected ActiveFeedRouteCommand(FeedCellListWidthProvider.FeedType route) {
      super(cachedRoute -> route);
   }
}
