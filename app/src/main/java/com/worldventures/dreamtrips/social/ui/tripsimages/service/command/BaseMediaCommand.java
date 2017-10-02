package com.worldventures.dreamtrips.social.ui.tripsimages.service.command;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs;

import java.util.List;

public abstract class BaseMediaCommand extends CommandWithError<List<BaseMediaEntity>> {
   private TripImagesArgs args;
   private boolean reload;
   private boolean loadMore;

   public BaseMediaCommand(TripImagesArgs args) {
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

   public List<BaseMediaEntity> getItems() {
      return getResult();
   }

   public abstract boolean lastPageReached();
}
