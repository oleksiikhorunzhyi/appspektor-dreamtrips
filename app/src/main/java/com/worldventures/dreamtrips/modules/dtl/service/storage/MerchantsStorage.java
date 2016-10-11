package com.worldventures.dreamtrips.modules.dtl.service.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedMemoryStorage;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantsAction;

import java.util.List;

public class MerchantsStorage implements ActionStorage<List<ThinMerchant>> {

   private final PaginatedMemoryStorage<ThinMerchant> merchantPaginatedStorage;

   public MerchantsStorage(PaginatedMemoryStorage<ThinMerchant> merchantPaginatedStorage) {
      this.merchantPaginatedStorage = merchantPaginatedStorage;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return MerchantsAction.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, List<ThinMerchant> data) {
      merchantPaginatedStorage.save(params, data);
   }

   @Override
   public List<ThinMerchant> get(@Nullable CacheBundle action) {
      return merchantPaginatedStorage.get(action);
   }
}
