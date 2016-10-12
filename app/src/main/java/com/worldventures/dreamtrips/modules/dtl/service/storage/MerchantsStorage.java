package com.worldventures.dreamtrips.modules.dtl.service.storage;

import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedMemoryStorage;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantsAction;

import java.util.List;

import javax.inject.Inject;

public class MerchantsStorage extends PaginatedMemoryStorage<ThinMerchant> implements ActionStorage<List<ThinMerchant>> {

   @Inject public MerchantsStorage() {}

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return MerchantsAction.class;
   }

}
