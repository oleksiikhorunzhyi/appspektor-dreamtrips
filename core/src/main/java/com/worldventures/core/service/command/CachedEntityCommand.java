package com.worldventures.core.service.command;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.model.CachedModel;

public abstract class CachedEntityCommand extends CommandWithError<CachedModel> {

   protected CachedModel cachedModel;

   public CachedEntityCommand(CachedModel cachedModel) {
      this.cachedModel = cachedModel;
   }

   public CachedModel getCachedModel() {
      return cachedModel;
   }
}
