package com.worldventures.dreamtrips.modules.dtl.domain.storage;

import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedMemoryStorage;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantsAction;

import java.util.List;

public class MerchantsStorage extends PaginatedMemoryStorage<ThinMerchant> implements ActionStorage<List<ThinMerchant>> {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return MerchantsAction.class;
   }

}
