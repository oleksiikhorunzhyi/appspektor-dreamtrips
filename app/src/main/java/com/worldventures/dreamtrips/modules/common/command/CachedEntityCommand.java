package com.worldventures.dreamtrips.modules.common.command;

import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.modules.video.model.CachedModel;

public abstract class CachedEntityCommand extends CommandWithError<CachedModel> {

   protected CachedModel cachedModel;

   public CachedEntityCommand(CachedModel cachedModel) {
      this.cachedModel = cachedModel;
   }

   public CachedModel getCachedModel() {
      return cachedModel;
   }
}
