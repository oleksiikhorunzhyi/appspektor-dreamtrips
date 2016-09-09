package com.worldventures.dreamtrips.modules.common.command;

import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

public abstract class CachedEntityCommand extends CommandWithError<CachedEntity> {

   protected CachedEntity cachedEntity;

   public CachedEntityCommand(CachedEntity cachedEntity) {
      this.cachedEntity = cachedEntity;
   }

   public CachedEntity getCachedEntity() {
      return cachedEntity;
   }
}
