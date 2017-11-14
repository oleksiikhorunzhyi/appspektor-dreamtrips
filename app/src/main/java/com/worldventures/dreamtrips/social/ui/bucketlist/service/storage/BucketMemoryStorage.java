package com.worldventures.dreamtrips.social.ui.bucketlist.service.storage;

import android.support.annotation.Nullable;

import com.worldventures.core.janet.cache.CacheBundle;
import com.worldventures.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BucketMemoryStorage extends MemoryStorage<List<BucketItem>> {

   private final Map<Integer, List<BucketItem>> cache = new ConcurrentHashMap<>();

   @Override
   public void save(@Nullable CacheBundle bundle, List<BucketItem> data) {
      checkBundle(bundle);
      int userId = bundle.get(BucketListDiskStorage.USER_ID_EXTRA);
      cache.put(userId, data);
   }

   @Override
   public List<BucketItem> get(@Nullable CacheBundle bundle) {
      checkBundle(bundle);
      int userId = bundle.get(BucketListDiskStorage.USER_ID_EXTRA);
      return cache.get(userId);
   }

   @Override
   public void clearMemory() {
      super.clearMemory();
      cache.clear();
   }

   protected void checkBundle(@Nullable CacheBundle bundle) {
      if (bundle == null) {
         throw new IllegalArgumentException("User id should been provided");
      }
      if (!bundle.contains(BucketListDiskStorage.USER_ID_EXTRA)) {
         throw new IllegalArgumentException("User id should been provided");
      }
   }
}
