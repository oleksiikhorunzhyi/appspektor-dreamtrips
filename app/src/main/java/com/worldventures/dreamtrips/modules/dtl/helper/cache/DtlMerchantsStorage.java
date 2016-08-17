package com.worldventures.dreamtrips.modules.dtl.helper.cache;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantsAction;

import java.util.List;

public class DtlMerchantsStorage implements ActionStorage<List<DtlMerchant>> {

   private final SnappyRepository db;

   public DtlMerchantsStorage(SnappyRepository db) {
      this.db = db;
   }

   @Override
   public void save(@Nullable CacheBundle bundle, List<DtlMerchant> data) {
      db.saveDtlMerhants(data);
   }

   @Override
   public List<DtlMerchant> get(@Nullable CacheBundle bundle) {
      return db.getDtlMerchants();
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return DtlMerchantsAction.class;
   }
}
