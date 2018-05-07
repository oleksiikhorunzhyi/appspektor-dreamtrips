package com.worldventures.dreamtrips.social.ui.bucketlist.service;

import com.worldventures.dreamtrips.modules.common.delegate.ReplayEventDelegate;
import com.worldventures.dreamtrips.modules.common.delegate.ReplayEventDelegatesWiper;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;

public class CurrentOpenTabEventDelegate extends ReplayEventDelegate<BucketItem.BucketType> {

   public CurrentOpenTabEventDelegate(ReplayEventDelegatesWiper wiper) {
      super(wiper);
   }
}
