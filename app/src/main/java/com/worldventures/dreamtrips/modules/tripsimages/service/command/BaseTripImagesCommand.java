package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.modules.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.TripImagesArgs;

import java.util.List;

public abstract class BaseTripImagesCommand extends CommandWithError<List<BaseMediaEntity>> {
   private TripImagesArgs args;
   private boolean reload;
   private boolean loadMore;

   public BaseTripImagesCommand(TripImagesArgs args) {
      this.args = args;
   }

   public boolean isReload() {
      return reload;
   }

   public void setReload(boolean reload) {
      this.reload = reload;
   }

   public boolean isLoadMore() {
      return loadMore;
   }

   public void setLoadMore(boolean loadMore) {
      this.loadMore = loadMore;
   }

   public TripImagesArgs getArgs() {
      return args;
   }

   public abstract boolean lastPageReached();
}
