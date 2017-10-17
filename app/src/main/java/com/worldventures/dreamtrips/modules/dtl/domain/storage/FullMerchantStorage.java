package com.worldventures.dreamtrips.modules.dtl.domain.storage;

import android.support.annotation.Nullable;

import com.worldventures.core.janet.cache.CacheBundle;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.core.janet.cache.storage.ClearableStorage;
import com.worldventures.core.janet.cache.storage.Storage;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.service.action.FullMerchantAction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FullMerchantStorage implements Storage<Merchant>, ActionStorage<Merchant>, ClearableStorage {

   public static final String BUNDLE_MERCHANT_ID = "BUNDLE_MERCHANT_ID";

   private final Map<String, Merchant> cache = new ConcurrentHashMap<>();

   @Override
   public synchronized void save(@Nullable CacheBundle params, Merchant merchant) {
      if (merchant != null) {
         cache.put(merchant.id(), merchant);
      }
   }

   @Override
   @Nullable
   public Merchant get(@Nullable CacheBundle action) {
      return action != null && action.contains(BUNDLE_MERCHANT_ID) ? cache.get(action.get(BUNDLE_MERCHANT_ID)) : null;
   }

   @Override
   public void clearMemory() {
      cache.clear();
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return FullMerchantAction.class;
   }
}
