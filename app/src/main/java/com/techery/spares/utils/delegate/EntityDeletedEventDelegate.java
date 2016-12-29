package com.techery.spares.utils.delegate;

import com.worldventures.dreamtrips.modules.common.delegate.ReplayEventDelegate;
import com.worldventures.dreamtrips.modules.common.delegate.ReplayEventDelegatesWiper;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;

public class EntityDeletedEventDelegate extends ReplayEventDelegate<FeedEntity> {

   public EntityDeletedEventDelegate(ReplayEventDelegatesWiper wiper) {
      super(wiper);
   }
}
