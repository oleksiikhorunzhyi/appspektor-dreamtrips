package com.worldventures.dreamtrips.modules.dtl.helper.cache;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantsAction;

import java.util.List;

public class ThinMerchantsStorage implements ActionStorage<List<ThinMerchant>> {

   private final SnappyRepository db;

   public ThinMerchantsStorage(SnappyRepository db) {
      this.db = db;
   }

   @Override
   public void save(@Nullable CacheBundle bundle, List<ThinMerchant> data) {
      db.saveThinMerchants(data);
   }

   @Override
   public List<ThinMerchant> get(@Nullable CacheBundle bundle) {
      return db.getThinMerchants();
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return DtlMerchantsAction.class;
   }
}
